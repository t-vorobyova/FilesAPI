package com.restfileapi;

import com.restfileapi.entries.FilePath;
import com.restfileapi.entries.Metadata;
import com.restfileapi.utils.MessageDigestSHA256;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tatyana on 14.03.2017.
 */
public class FileProvider {

    private File baseDir = new File(System.getProperty("user.home"), "FilesAPIRoot");

    private File baseDirHash = new File(System.getProperty("user.home"), "FilesAPIHash");

    private static FileProvider instance = new FileProvider();

    public static FileProvider getInstance() {
        return instance;
    }

    private FileProvider() {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        if(!baseDirHash.exists()){
            baseDirHash.mkdir();
        }
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getBaseDirHash() {
        return baseDirHash;
    }

    public Metadata getMetadata(FilePath filePath) throws IOException, NoSuchAlgorithmException {
        File item;
        try {
            item = new File(baseDir, filePath.getPath());
        }catch (NullPointerException exc){
            throw exc;
        }
        if (item.exists()) {
            String hash = getHash(filePath.getPath());
            return new Metadata(item.getName(), item.length(), item.lastModified(), hash);
        } else {
            throw new FileNotFoundException();
        }
    }

    public Metadata getMetadata(String filePath) throws IOException, NoSuchAlgorithmException {
        File item;
        try {
            item = new File(baseDir, filePath);
        }catch (NullPointerException exc){
            throw exc;
        }
        if (item.exists()) {
            String hash = getHash(filePath);
            return new Metadata(item.getName(), item.length(), item.lastModified(), hash);
        } else {
            throw new FileNotFoundException();
        }
    }

    public void delete(FilePath filePath) throws FileNotFoundException {
        File item = new File(baseDir, filePath.getPath());
        if (item.exists()) {
            if(item.delete()) {
                deleteHash(filePath.getPath());
                return;
            }else{
                throw new SecurityException();
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    public List<Metadata> getListFolder() throws IOException, NoSuchAlgorithmException {
        List<Metadata> metadatas = new ArrayList<>();
        for (File item : baseDir.listFiles()) {
            String hash = getHash(item.getName());
            metadatas.add(new Metadata(item.getName(), item.length(), item.lastModified(), hash));
        }
        return metadatas;
    }

    public InputStream getFileInputStream(FilePath filePath) throws FileNotFoundException {
        return new FileInputStream(new File(baseDir, filePath.getPath()));

    }

    public String upload(InputStream inputStream, String path, boolean autorename) throws IOException {
        File file = new File(baseDir, path);
        String hash;
        if(file.exists()) {
            if (!autorename) {
                java.nio.file.Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                hash = setHash(path);
            } else {
                int i=0;
                while ((file=new File(baseDir,path+"("+i+")")).exists())
                    i++;
                java.nio.file.Files.copy(inputStream, file.toPath());
                hash = setHash(file.getName());
            }
        }else{
            java.nio.file.Files.copy(inputStream, file.toPath());
            hash = setHash(path);
        }
        return file.getName();
    }

    public String getHash(String path){
        File file = new File(baseDirHash,path);
        String hash=null;
        if(file.exists()){
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                hash = new String(data);
            }catch (IOException exc){
                exc.printStackTrace();
            }

        }else{
            hash = setHash(path);
        }
        return hash;
    }

    private String setHash(String path){
        File file = new File(baseDirHash,path);
        File item = new File(baseDir,path);
        String hash = null;
        try(OutputStream outputStream = new FileOutputStream(file)){
            hash = MessageDigestSHA256.hash256(item);
            outputStream.write(hash.getBytes());
            outputStream.flush();
            outputStream.close();
        }catch (Exception exc){
            exc.printStackTrace();
        }
        return hash;
    }

    private void deleteHash(String path){
        File file = new File(baseDirHash,path);
        if(file.exists()){
            file.delete();
        }


    }

}
