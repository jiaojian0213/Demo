package com.example.jiao.demo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.example.jiao.demo.Constants;

import java.io.File;
import java.io.IOException;


/**
 * FileUtils
 * Created by Yancy on 2015/12/2.
 */
public class FileUtils {

    private final static String PATTERN = "yyyyMMddHHmmss";
    private String SDPATH = Environment.getExternalStorageDirectory()+"/Demo/";


    public static File createTmpFile(Context context) {
        return createTmpFile(context,null);
    }

    public static File createFile(Context context,String dirName,String fileName) {
        if(TextUtils.isEmpty(fileName)) {
            String timeStamp = "" + System.currentTimeMillis();
            fileName = timeStamp + ".jpg";
        }
        File file = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(FileUtils.getSDPath() + "/Demo");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            dir = new File(dir, "/"+dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, fileName);
            if (file.exists()) {
                file.delete();
            }
            return file;
        }else{
            file = new File(context.getFilesDir(), fileName);
        }
        return file;
    }

    public static File createTmpFile(Context context,String fileName) {
        return createFile(context,"tempimages",fileName);
    }

    public static File createImageFile(Context context,String fileName) {
        return createFile(context,"images",fileName);
    }

    public static File createPrintFile(Context context,String fileName) {
        return createFile(context,"print",fileName);
    }

    //判断是否存在sd卡并返回sd卡路径
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }else{
            sdDir = Constants.context.getCacheDir();
        }
        return sdDir.toString();
    }

    public static File getImagePath(Context context) {
        File file = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

        }else{

        }
        return file;
    }

    /**
     * 在SD卡上创建文件
     * @param fileName
     * @return
     */
    public File createSdFile(String fileName){
        File file = new File(SDPATH + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 创建SD卡目录
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName){
        File file = new File(SDPATH + dirName);
        file.mkdir();

        return file;
    }

    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);

        return file.exists();

    }




}