package com.zzx.utils;

import com.zzx.config.ImgUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 文件操作工具
 */
@Component
public class FileUtil {
    @Autowired
    private ImgUploadConfig imgUploadConfig;

    /**
     * 获取可用的文件保存路径
     * 当所有路径文件夹单位数都超过FolderSize时，返回null
     *
     * @return
     */
    public String getSavePath() {

        ConcurrentLinkedQueue<File> availablePath = ImgUploadConfig.getAvailablePath();
        Iterator<File> iterator = availablePath.iterator();
       
        while (iterator.hasNext()) { 
			File file = iterator.next();
            if (file.listFiles().length < imgUploadConfig.getFolderSize()) {
                return file.getPath();
            } else {
                availablePath.remove(file);
            }
        }
        return null;
    }

    /**
     * 初始化上传文件夹
     * ！操作非常耗时
     *
     * @return
     */
    public List<File> initUploadFolder() {
        File root = new File(imgUploadConfig.getUploadFolder());
        root.mkdirs();
        LinkedList<File> files = new LinkedList<>();
        files.add(root);

        for (int i = 0; i < imgUploadConfig.getLayerCount(); i++) {
            LinkedList<File> filesClone = (LinkedList<File>)files.clone();
            for (File file : filesClone) {
                files.addAll(createFolder(file.getPath(), imgUploadConfig.getFolderSize()));//addAll 添加到链表末尾
            }
        }

        return files;
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @param folderSize 文件夹个数
     */
    private List<File> createFolder(String path, int folderSize) {
        LinkedList<File> files = new LinkedList<>();
        for (int i = 1; i <= folderSize; i++) {
            File file = new File(path + "/" + i);
            file.mkdirs();
            files.add(file);
        }
        return files; //返回创建的文件夹
    }


    /**
     * 获取上传文件夹的最下层路径
     *
     * @return
     */
    public List<File> getAllFolder() {
        LinkedList<File> files = new LinkedList<>();
        File root = new File(imgUploadConfig.getUploadFolder());
        files.add(root);
        for (int i = 0; i < imgUploadConfig.getLayerCount(); i++) {
            LinkedList<File> filesClone = (LinkedList<File>)files.clone();
            for (File file : filesClone) {
                files.removeFirst();
                Collections.addAll(files, file.listFiles());
            }
        }
        return files;
    }

    /**
     * inputStream 转 File
     */
    static File inputStreamToFile(InputStream ins, String name) throws Exception{
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }
}
