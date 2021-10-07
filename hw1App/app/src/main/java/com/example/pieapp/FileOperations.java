package com.example.pieapp;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import java.util.stream.Collectors;

        import android.os.Environment;
        import android.util.Log;

public class FileOperations {

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

            Log.d("Success","Success");
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
