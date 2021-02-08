package com.example.calyx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;



public class Usr_Wind extends AppCompatActivity {
    private static final String TAG = "USER_WIND";
    private static final int PERMISSION_REQUEST_CODE = 1; //код запрашиваемого разрешения
    private static final int REQUEST_CODE_PICK_FILE = 1;

    private String file_path;
    private User user;
    private JSONArray JSONlist;
    private ArrayList<String> listfiles;
    private ListView list;
    private DataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usr_wind);

        Bundle arguments = getIntent().getExtras();

        user = new User(arguments.get("login").toString(),
                        "",
                        arguments.get("userKey").toString());

        setTitle(user.getLogin());

        updateList(); //обновление списка файлов

    }

    private void updateList() {
        //Запрос на получение списка файлов
        HTTPRequest files = new HTTPRequest(user, HTTPRequest.FILELIST);
        files.setContext(this);

        if (!files.execute()) Toast_creator.showToast(this, "Ошибка соединения с сервером!");
        else {

            if(!files.getResponseText().contains("No files")) {
                try {
                    JSONlist = new JSONArray(files.getResponseText());
                } catch (JSONException e) {
                    System.out.println(e);
                }

                //Распарсим JSON
                listfiles = new ArrayList<>();
                for (int i = 0; i < JSONlist.length(); i++) {
                    try {
                        listfiles.add(JSONlist.getString(i).substring(2, JSONlist.getString(i).length() - 2));
                        //System.out.println(listfiles.get(i));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                //Установим адаптер
                list = (ListView) findViewById(R.id.fileList);
                adapter = new DataAdapter(this, listfiles);
                list.setAdapter(adapter);
                list.setLongClickable(true);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        TextView fileName = (TextView) itemClicked.findViewById(R.id.file_name);

                        HTTPRequest download = new HTTPRequest(fileName.getText().toString(), user, HTTPRequest.DOWNLOAD);
                        download.setContext(getApplicationContext());
                        if(!download.execute()) Toast_creator.showToast(getApplicationContext(),"Ошибка соединения с сервером!");
                    }
                });

                list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        TextView fileName = (TextView) itemClicked.findViewById(R.id.file_name);

                        HTTPRequest drop = new HTTPRequest(fileName.getText().toString(), user, HTTPRequest.DROP);
                        drop.setContext(getApplicationContext());
                        if (!drop.execute()) Toast_creator.showToast(getApplicationContext(), "Ошибка соединения с сервером!");
                        else{
                            String selectedItem = parent.getItemAtPosition(position).toString();

                            adapter.remove(selectedItem);
                            adapter.notifyDataSetChanged();
                        }

                        return true;
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_usr_wind, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.exit:
                //Очищение данных пользователя
                SharedPreferences.Editor editor = MainActivity.getSettings().edit();
                editor.clear();
                editor.apply();

                Intent restartMain = new Intent(this, MainActivity.class);
                startActivity(restartMain);

                finish();
                break;

            case R.id.update:
                updateList();
                break;
        }
        return true;
    }


    private void requestPermissions() { // запрос разрешения
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }


    //ДЛЯ ОБРАБОТКИ РАЗРЕШЕНИЯ
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//если дали разрешение
                Intent startFileManager = new Intent(this, FilePickerActivity.class); //запустить новое активити
                startActivity(startFileManager);

            } else { // если не дали разрешение
                AlertDialog dialog = Dialog_wnd.createDialog(this, Dialog_wnd.PERMISSION_READ);
                dialog.show(); //покажем окошко с предупреждением
            }
        }
    }

    //КНОПА ДЛЯ ЗАГРУЗКИ НОВОГО ФАЙЛА
    public void on_Click_files(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { // Разрешение НЕ предоставлено
            requestPermissions(); //то спросить разрешения
            //ВЫЗОВ onRequestPermissionsResult

        } else {// если разрешение уже есть
            Intent startFileManager = new Intent(this, FilePickerActivity.class);
            startActivityForResult(startFileManager, REQUEST_CODE_PICK_FILE); // запуск нового активити
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            file_path = data.getStringExtra(FilePickerActivity.CHOSEN_FILE_PATH);
            Log.i(TAG, file_path);

            HTTPRequest upload = new HTTPRequest(file_path, user, HTTPRequest.UPLOAD);
            upload.setContext(this);
            if(!upload.execute()) Toast_creator.showToast(getApplicationContext(), "Ошибка соединения с сервером!");
            else updateList();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = MainActivity.getSettings().edit();
        editor.clear();
        editor.apply();

        Intent restartMain = new Intent(this, MainActivity.class);
        startActivity(restartMain);
        finish();
    }
}
