package com.example.pieapp;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.security.InvalidKeyException;
        import java.security.NoSuchAlgorithmException;
        import java.security.spec.InvalidKeySpecException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.stream.Collectors;

        import android.util.Log;

        import javax.crypto.Cipher;
        import javax.crypto.CipherInputStream;
        import javax.crypto.CipherOutputStream;
        import javax.crypto.NoSuchPaddingException;
        import javax.crypto.SecretKey;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.PBEKeySpec;
        import javax.crypto.spec.SecretKeySpec;

public class FileEncryptionAgent {

    String rootpath, passphrase, algorithm;
    SecretKey skey;
    SecretKeySpec skeySpec;
    Cipher encrypter, decrypter;

    public FileEncryptionAgent(String fileroot, String pass, String alg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        // instantiate class member vars
        rootpath = fileroot;
        passphrase = pass;
        algorithm = alg;

        // instantiate key based on passphrase
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] salt = {0, 1, 2, 3};
        PBEKeySpec spec = new PBEKeySpec(
                passphrase.toCharArray(),
                salt,
                1,
                256
                );
        SecretKey skey = factory.generateSecret(spec);
        skeySpec = new SecretKeySpec(skey.getEncoded(), algorithm);

        // instantiate encrypter & decrypter Cipher objects
        encrypter = Cipher.getInstance(algorithm);
        encrypter.init(Cipher.ENCRYPT_MODE, skeySpec);

        decrypter = Cipher.getInstance(algorithm);
        decrypter.init(Cipher.DECRYPT_MODE, skeySpec);
    }

    public boolean encryptFiles() {
        boolean success = true;
        for (File file : getFiles()) {
            success = success && encryptFile(file);
        }
        return success;
    }

    public boolean decryptFiles() {
        boolean success = true;
        for (File file : getFiles()) {
            success = success && decryptFile(file);
        }
        return success;
    }

    public String getPassphrase() {
        return passphrase;
    }

    private boolean encryptFile(File file) {
        try {
            String inpath = file.getAbsolutePath();
            File out = new File(inpath + ".enc");
            if(!out.exists()) {
                out.createNewFile();
            }

            FileInputStream fis = new FileInputStream(inpath);
            FileOutputStream fos = new FileOutputStream(out.getAbsolutePath());
            CipherOutputStream cos = new CipherOutputStream(fos, encrypter);

            byte[] plaintextChunk = new byte[8];
            int i;
            while((i = fis.read(plaintextChunk)) != -1) {
                cos.write(plaintextChunk, 0, i);
            }
            cos.flush();
            cos.close();
            fis.close();
            fos.close();

            file.delete();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean decryptFile(File file) {
        try {
            String inpath = file.getAbsolutePath();
            String inpathNoExtension = inpath.substring(0, inpath.lastIndexOf("."));
            String inpathExtension = inpath.substring(inpath.lastIndexOf("."));

            if(!inpathExtension.trim().equals(".enc")) {
                Log.d("decryptFile", "No .enc extension found on decryption");
                return false;
            }

            File out = new File(inpathNoExtension);
            if(!out.exists()) {
                out.createNewFile();
            }

            FileInputStream fis = new FileInputStream(inpath);
            FileOutputStream fos = new FileOutputStream(out.getAbsolutePath());
            CipherInputStream cis = new CipherInputStream(fis, decrypter);

            byte[] cipherChunk = new byte[8];
            int i;
            while((i = cis.read(cipherChunk)) != -1) {
                fos.write(cipherChunk, 0, i);
            }
            fos.flush();
            cis.close();
            fis.close();
            fos.close();

            file.delete();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<File> getFiles() {
        File rootdir = new File(rootpath);
        ArrayList<File> list = new ArrayList<>();

        if(rootdir.exists() && rootdir.isDirectory()) {
            for (File file : rootdir.listFiles()) {
                if(file.exists() && file.isFile()) {
                    list.add(file);
                }
            }
        }

        return list;
    }

    public String getFileNames() {
        List<String> fnames = getFiles().stream().map(f -> f.getPath()).collect(Collectors.toList());
        return fnames.stream().reduce("", (s1,s2) -> s1 + ", " + s2);
    }

//    private ArrayList<File> getFilesRecursive(String path) {
//        File file = new File(path);
//        ArrayList<File> list = new ArrayList<>();
//        if(file.exists()) {
//            if(file.isDirectory()) {
//                for (File child : file.listFiles()) {
//                    list.addAll(getFilesRecursive(child.getPath()));
//                }
//
//            } else if (file.isFile()) {
//                list.add(file);
//            }
//        }
//        return list;
//    }
}
