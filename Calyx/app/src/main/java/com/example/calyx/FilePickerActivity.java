package com.example.calyx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.File;
import java.util.List;


public class FilePickerActivity extends AppCompatActivity {
   private FileManager fileManager;
   private FilesAdapter adapter;

   public static final String CHOSEN_FILE_PATH = "file_path";//КЛЮЧ ДЛЯ ПЕРЕДАЧИ ПУТИ К ФАЙЛУ

   //СЛУШАТЕЛЬ СОБЫТИЯ "ПРИЛЕТЕЛ ФАЙЛИК"
   private final FilesAdapter.OnFileClickListener onFileClickListener = new FilesAdapter.OnFileClickListener() {
        @Override
        public void onFileClick(File file) { //приняли событие

            //И ЧТО С ЭТИМ ДЕЛАТЬ ТЕПЕРЬ?
            if (file.isDirectory()) { //если кликнули на ДИРЕКТОРИЮ
                fileManager.setCurrentDirectory(file);//переходим в эту директорию
                updateFileList();//и обновляем список на экране
            }
            else{ //если кликнули на ФАЙЛ
                Intent intent = new Intent(); //создаем интент
                intent.putExtra(CHOSEN_FILE_PATH, file.getAbsolutePath()); // вместе с интентом передаем путь к файлу
                setResult(RESULT_OK, intent);//так же сообщаем, что все прошло нормально
                finish();//возвращаемся к прошлой активити
            }
        }
   };


   //КОНСТРУКТОР
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        RecyclerView recyclerView = findViewById(R.id.files_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Выбор файла");

        //установка адаптера, чтоббыло красиво и тык тык
        adapter = new FilesAdapter();
        recyclerView.setAdapter(adapter);

        //запуск создания списка файлов
        initFileManager();
        updateFileList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.setOnFileClickListener(onFileClickListener);
    }


    // ПРИ ОСТАНОВКЕ АКТИВИТИ ОТПИСЫВАЕМСЯ ОТ ПОЛУЧЕНИЙ СОБЫТИЙ
    @Override
    protected void onStop() {
        adapter.setOnFileClickListener(null);
        super.onStop();
    }


    //СОЗДАЕМ ФАЙЛОВЫЙ МЕНЕДЖЕР
    private void initFileManager() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) { // Разрешение предоставлено

            fileManager = new FileManager(this);
        }
    }

    //ОБНОВЛЕНИЕ СПИСКА, ЕСЛИ ЖМЯКНУЛИ ПО ПАПКЕ
    private void updateFileList() {
        List<File> files = fileManager.getFiles();

        adapter.setFiles(files);
        adapter.notifyDataSetChanged();
    }

    //ПРИ ЗАПУСКЕ АКТИВИТИ УСТАНАВЛИВАЕМ СЛУШАТЕЛЬ В АДАПТЕР


    @Override
    public void onBackPressed() {
        // если файловый менеджер создан и  можно подняться выше (т.е мы не в rootDir)
        if (fileManager != null && fileManager.navigateUp()) {
            updateFileList();//обновить список
        }
        else {//если мы в rootDir, то вернуться в активити
            super.onBackPressed();
        }
    }

}
