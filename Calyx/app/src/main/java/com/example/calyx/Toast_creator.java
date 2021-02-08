package com.example.calyx;

import android.content.Context;
import android.widget.Toast;

public class Toast_creator {
    public static void showToast (Context context, String text){
        Toast msg = Toast.makeText(context, text, Toast.LENGTH_LONG);
        msg.show();
    }
}
