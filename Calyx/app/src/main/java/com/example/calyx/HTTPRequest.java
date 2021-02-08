package com.example.calyx;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HTTPRequest {
    private static final String TAG = "HTTP_REQUEST"; //Для ошибок

    public static final int REGISTRATION = 0;
    public static final int AUTHOR = 1;
    public static final int UPLOAD = 2;
    public static final int DOWNLOAD = 3;
    public static final int FILELIST = 4;
    public static final int DROP = 5;

    private static final String URL = "https://calyx.space";
    private static final String REG = "/registration.php";
    private static final String LOG = "/login.php";
    private static final String FILES = "/filelist.php";
    private static final String UPLD = "/upload.php";
    private static final String DWNL = "/download.php";
    private static final String DRP = "/drop.php";

    private String uri;
    private RequestBody formBody;
    private Request request;
    private Response response;
    private String responseText;

    private int task;
    private User user;
    private String path;
    private File file;

    private boolean success;
    private String msg;
    private Context context;

    //КОНСТРУКТОР ДЛЯ РЕГИСТРАЦИИ,ВХОДА и ПОЛУЧЕНИЯ СПИСКА ФАЙЛОВ
    public HTTPRequest(User user, int task) {
        this.user = user;
        this.task = task;
        responseText = "";
        path = null;
        file = null;
        msg = "";
        success = true;
    }

    //КОНСТРУКТОР ДЛЯ ПОЛУЧЕНИЯ ФАЙЛА С СЕРВЕРА, ИЛИ ЗАГРУЗКИ
    public HTTPRequest(String path, User user, int task) {
        this.path = path;
        this.user = user;
        this.task = task;
        responseText = "";
        success = true;

        if(task == UPLOAD){
            file = new File(path);
            try {
                URLEncoder.encode(file.getName(), "utf-8");
            }
            catch (UnsupportedEncodingException e){Log.e(TAG, "Кодировочка-то говно!");}
        }

    }


    public void setContext(Context context) {
        this.context = context;
    }

    public User getUser() {
        return this.user;
    }

    public String getResponseText() {return responseText;}

    private void doInBackground() {

        Thread thread = new Thread(new Runnable() {
            public void run() {

                OkHttpClient client = new OkHttpClient();

                switch (task) { //составление адреса php-скрипта в зависимости от задачи
                    case REGISTRATION:
                        uri = URL + REG;
                        formBody = new FormBody.Builder()
                                .add("login", user.getLogin())
                                .add("password", user.getPassword())
                                .build();
                        break;

                    case AUTHOR:
                        uri = URL + LOG;
                        formBody = new FormBody.Builder()
                                .add("login", user.getLogin())
                                .add("password", user.getPassword())
                                .build();
                        break;

                    case UPLOAD:
                        uri = URL + UPLD;
                        try{
                        formBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", URLEncoder.encode(file.getName(),"unicode"),
                                        RequestBody.create(MediaType.parse("multipart/form-data"),file))
                                .addFormDataPart("userKey", user.getUserKey())
                                .addFormDataPart("originalName", file.getName())
                                .build();
                        }
                        catch(UnsupportedEncodingException e){
                            Log.e(TAG, "Кодировка не поддерживается");
                        }
                        break;

                    case FILELIST:
                        uri = URL + FILES;
                        formBody = new FormBody.Builder()
                                .add("userKey", user.getUserKey())
                                .build();
                        break;

                    case DOWNLOAD:
                        uri = URL + DWNL;
                        formBody = new FormBody.Builder()
                                .add("userKey", user.getUserKey())
                                .add("name", path)
                                .build();
                        break;

                    case DROP:
                        uri = URL + DRP;
                        formBody = new FormBody.Builder()
                                .add("userKey", user.getUserKey())
                                .add("name", path)
                                .build();
                        break;

                }

                request = new Request.Builder() //Формирование запроса
                        .url(uri)
                        .post(formBody)
                        .build();


                try {
                    response = client.newCall(request).execute();
                    success = true;
                }
                catch (IOException e) {
                    Log.e(TAG, "Невозможно выполнить запрос (142)");
                    success = false;
                }

                if (success == true) { //если получилось установить соединение

                    try {
                        responseText = response.body().string();
                    } catch (IOException e) {
                        Log.e(TAG, "Невозможно получить результат запроса (154)");
                    }

                    //Обработка того, что пришло в ответ
                    switch (task) {
                        case REGISTRATION:
                            if (responseText.contains("Duplicate")) msg = "Пользователь с таким логином или паролем уже зарегистрирован!";
                            else msg = "Регистрация успешно завершена!";
                            break;

                        case AUTHOR:
                            if (!responseText.contains("Deny")) {
                                user.setUserKey(responseText); //установка ключа пользователя
                                Log.i(TAG, user.getUserKey() + " Вход прошел, ключ получен.");
                                msg = "Авторизация выполнена успешно.";
                            } else { //если получен отказ (Deny)
                                user.setUserKey("Deny");
                                Log.i(TAG, responseText + " Не удалось войти, ключа нет. " + user.getUserKey());
                                msg = "Неверный логин или пароль!";
                            }
                            break;

                        case FILELIST:
                            Log.i(TAG, responseText + " - Список файлов.");
                            if (responseText.contains("No files")) {
                                msg = "Вы еще не загрузили ни одного файла.";
                            }
                            break;

                        case UPLOAD:
                            Log.i(TAG, responseText + " - Загрузка файла бросила мне палку.");
                            if (responseText.contains("Success")) msg = "Файл успешно загружен.";
                            else msg = "Не удалось загрузить файл!";
                            break;

                        case DOWNLOAD:
                            Log.i(TAG, responseText + " - Ссылка");
                            if (responseText != "") {
                                request = new Request.Builder().url(responseText).build();

                                try {
                                    response = client.newCall(request).execute();
                                }
                                catch (IOException e) {
                                    Log.e(TAG, "Не удалось скачать файл.");
                                    msg = "Не удалось скачать файл.";
                                }

                                Log.i(TAG, Environment.getExternalStorageDirectory().getAbsolutePath() + "- Путь");
                                //Запись полученного по URL файла
                                File output = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + path);
                                try {
                                    FileOutputStream out = new FileOutputStream(output);
                                    out.write(response.body().bytes());
                                    out.flush();
                                    out.close();
                                }
                                catch (FileNotFoundException e) {
                                    Log.e(TAG, e.getMessage());
                                    msg = "Директория для сохранения не найдена.";
                                }
                                catch (IOException e) {
                                    Log.e(TAG, "Невозможно записать файл.");
                                    msg = "Невозможно записать файл.";
                                }
                                msg = "Файл успешно загружен!\n"+output.getAbsolutePath();
                            }
                            break;

                        case DROP:
                            if(responseText.contains("Success")) {
                                msg = "Файл успешно удален.";
                                success = true;
                            }
                            else {
                                msg = "Не удалось удалить файл!";
                                success = false;
                            }
                    }
                    client.connectionPool().evictAll();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        }
        catch(InterruptedException e){
            Log.e(TAG, "Поток сломался");
        }
    }

    public boolean execute() {
        doInBackground();
        if((task != FILELIST)&&(msg!="")) {
            Toast_creator.showToast(context, msg);
        }
        return success;
    }
}

