package com.example.calyx;

import android.content.Context;

public class Log_Pass {
    public static final int LOGIN = 0;
    public static final int PASSWORD = 1;

    private static String checkLength(String str, int type){

        String warning = "";
        switch (type) {
            case LOGIN:
                if ((str.length() < 6) || (str.length() > 32)) {
                    warning = "Логин должен содержать от 6 до 32 символов!\n";
                }
                break;
            case PASSWORD:
                if ((str.length() < 6) || (str.length() > 32)) {
                    warning = "Пароль должен содержать от 8 до 32 символов!\n";
                }
                break;
        }
        return warning;
    }

    private static String frbdnCharacters(String str){
        String warning ="";
        if (str.contains(" ") || str.contains("/") ||
                (str.contains("\'")) || str.contains("\"")){
            warning +="Логин и пароль НЕ МОГУТ содержать следующие символы: \' \" / и пробелы!";
        }
        return warning;
    }

    public static boolean checkLogPass(String log, String pass, Context context) {

        String warning = checkLength(log, Log_Pass.LOGIN)+checkLength(pass, Log_Pass.PASSWORD);
        if (frbdnCharacters(log) != ""){
            warning += frbdnCharacters(log);
        }
        else{
            warning += frbdnCharacters(pass);
        }

        if (warning != "") { //Если есть какие то замечания
            Toast_creator.showToast(context,warning);
            return false;
        }
        else{
            return true;
        }
    }
}
