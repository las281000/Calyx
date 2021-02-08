package com.example.calyx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent; // подключаем класс Intent
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 0;
    private EditText username;
    private EditText password;

    private User user;

    //для автоматической авторизации
    private static final String APP_PREFERENCES = "settings";
    private static final String APP_PREFERENCES_LOGIN = "login";
    private static final String APP_PREFERENCES_PASSWORD = "password";
    private static SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) (findViewById(R.id.login_Edit));
        password = (EditText) (findViewById(R.id.password_Edit));

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //если пароль был ранее сохранен
        if ((settings.contains(APP_PREFERENCES_LOGIN))&(settings.contains(APP_PREFERENCES_LOGIN))){
            user = new User(settings.getString(APP_PREFERENCES_LOGIN, ""),
                            settings.getString(APP_PREFERENCES_PASSWORD,""),
                            "");

            HTTPRequest author = new HTTPRequest(user, HTTPRequest.AUTHOR);
            author.setContext(this);

            if (!author.execute()) Toast_creator.showToast(this, "Ошибка соединения с сервером!");
            else{
                Intent signIn = new Intent(this, Usr_Wind.class);
                signIn.putExtra("login", user.getLogin());
                signIn.putExtra("password", "");
                signIn.putExtra("userKey", user.getUserKey());
                startActivity(signIn);
                finish();
            }
        }
        else {
            username = (EditText) (findViewById(R.id.login_Edit));
            password = (EditText) (findViewById(R.id.password_Edit));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        username.setText(null);
        password.setText(null);
    }

    public static SharedPreferences getSettings() {
        return settings;
    }

    //КНОПКА РЕГИСТРАЦИИ
    public void on_Click_reg(View view) {
        Intent reg = new Intent(this, Registration.class);
        startActivity(reg);
    }

    //КНОПКА ВХОДА В АККАУНТ
    public void on_Click_sighIn(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) { // Разрешение НЕ предоставлено
            requestPermissions(); //то спросить разрешения
        }
        else {// если разрешение уже есть

            //проверяем пароль и логин
            if (!Log_Pass.checkLogPass(username.getText().toString(), password.getText().toString(), getApplicationContext())) {
                username.setText(null);
                password.setText(null);
            }
            else { //делаем запрос на сервер
                user = new User(username.getText().toString(), password.getText().toString(), "");
                HTTPRequest author = new HTTPRequest(user, HTTPRequest.AUTHOR);
                author.setContext(this);

                if (!author.execute()) Toast_creator.showToast(this, "Ошибка соединения с сервером!");
                else {
                    if (author.getUser().getUserKey().contains("Deny")) {
                        username.setText(null);
                        password.setText(null);
                    } else {
                        //записываем правильные логин и пароль в файл настроек
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_LOGIN,user.getLogin());
                        editor.putString(APP_PREFERENCES_PASSWORD,user.getPassword());
                        editor.apply();

                        //вызываем следующий активити
                        Intent signIn = new Intent(this, Usr_Wind.class);
                        signIn.putExtra("login", user.getLogin());
                        signIn.putExtra("password", "");
                        signIn.putExtra("userKey", user.getUserKey());
                        startActivity(signIn);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//если дали разрешение
                user = new User(username.getText().toString(), password.getText().toString(), "");

                HTTPRequest author = new HTTPRequest(user, HTTPRequest.AUTHOR);
                author.setContext(this);

                if (!author.execute()) Toast_creator.showToast(this, "Ошибка соединения с сервером!");
                else {
                    if (author.getUser().getUserKey().contains("Deny")) {
                        username.setText(null);
                        password.setText(null);
                    } else {
                        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_LOGIN, user.getLogin());
                        editor.putString(APP_PREFERENCES_PASSWORD, user.getPassword());
                        editor.apply();

                        Intent signIn = new Intent(this, Usr_Wind.class);
                        signIn.putExtra("login", user.getLogin());
                        signIn.putExtra("password", "");
                        signIn.putExtra("userKey", user.getUserKey());
                        startActivity(signIn);
                    }

                }

            } else { // если не дали разрешение
                AlertDialog dialog = Dialog_wnd.createDialog(this, Dialog_wnd.PERMISSION_WRITE);
                dialog.show(); //покажем окошко с предупреждением
            }
        }
    }

    private void requestPermissions() { // запрос разрешения на запись файлов
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }


}
