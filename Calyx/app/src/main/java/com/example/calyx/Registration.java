package com.example.calyx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Registration extends AppCompatActivity {
    private EditText login_edit;
    private EditText password_edit;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Регистрация");
        login_edit = (EditText) (findViewById(R.id.name_ed));
        password_edit = (EditText) (findViewById(R.id.password_ed));
    }

    //ОБРАБОТЧИК КНОПКИ
    public void on_Click_SignUp(View view) {

        if (!Log_Pass.checkLogPass(login_edit.getText().toString(), password_edit.getText().toString(), getApplicationContext())){
            login_edit.setText(null);
            password_edit.setText(null);
        }

        else {
            user = new User(login_edit.getText().toString(),
                            password_edit.getText().toString(),
                            null);

            HTTPRequest reg = new HTTPRequest(user, HTTPRequest.REGISTRATION);
            reg.setContext(this);

            if (!reg.execute()) Toast_creator.showToast(this, "Ошибка соединения с сервером!");
            else {
                //Перезапуск акивити входа
                Intent restartMain = new Intent(this, MainActivity.class);
                startActivity(restartMain);
                finish();
            }
        }
    }

}
