package com.example.calyx;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class Dialog_wnd {
    public static final int PERMISSION_READ = 0;
    public static final int PERMISSION_WRITE = 1;

    public static AlertDialog createDialog(Activity activity, int ID) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);

        builder.setTitle("Внимание");
        switch (ID) {
            case PERMISSION_READ:
                builder.setMessage("Для использования функционала данного приложения необходимо разрешить доступ к файлам устройства.");
                break;

            case PERMISSION_WRITE:
                builder.setMessage("Для использования функционала данного приложения необходимо разрешить запись файлов на ваше устройство.");
                break;
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Отпускает диалоговое окно
            }
        });
        return builder.create();
    }
}
