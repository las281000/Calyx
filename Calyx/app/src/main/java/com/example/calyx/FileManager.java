package com.example.calyx;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public File current_Directory;
    private final File root_Directory;

    public FileManager(Context context){
        File directory;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directory = Environment.getExternalStorageDirectory();
        } else {
            directory = ContextCompat.getDataDir(context);
        }
        root_Directory = directory;
        setCurrentDirectory(directory);
    }

    //ПЕРЕМЕЩЕНИЕ В ДИРЕКТОРИЮ (УСТАНОВКА ТЕКУЩЕЙ ДИРЕКТОРИИ)
    public boolean setCurrentDirectory(File directory) {

        if (!directory.isDirectory()) {// Если это не директория
            return false;
        }
        // Если влезли куда не надо
        if (!directory.equals(root_Directory) && root_Directory.getAbsolutePath().contains(directory.getAbsolutePath())) {
            return false;
        }

        current_Directory = directory;
        return true;
    }

    //BACK TO THE ПРОШЛАЯ ДИРЕКТОРИЯ
    public boolean navigateUp() {
        return setCurrentDirectory(current_Directory.getParentFile());
    }

    //СОЗДАНИЕ СПИСКА ФАЙЛОВ ДИРЕКТОРИИ
    public List<File> getFiles() {
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(current_Directory.listFiles()));// Добываем список файлов и поддиректорий

        return files;
    }




}
