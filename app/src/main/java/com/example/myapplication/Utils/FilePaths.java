package com.example.myapplication.Utils;
import android.os.Environment;


public class FilePaths {

//"storage/emulated/0"
public static  String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
public static String PICTURES = ROOT_DIR + "/Pictures";
public static  String CAMERA = ROOT_DIR + "/DCIM/camera";
public static String FIREBASE_IMAGE_STORAGE ="photos/users/";
        }