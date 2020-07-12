package com.example.projectbudgetmanager.back_end;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.projectbudgetmanager.R;

import com.example.projectbudgetmanager.back_end.controller.DangKi;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    Button btn_login, btn_send_register;
    static String URL_REGIST = "https://api-backend-nhom15.herokuapp.com/api/reg";
    EditText et_username_register, et_password_register, et_confirm_password_register, et_phone_register;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username_register = (EditText) findViewById(R.id.et_username_register);
        et_password_register = (EditText) findViewById(R.id.et_password_register);
        et_confirm_password_register = (EditText) findViewById(R.id.et_confirm_password_register);
        btn_login = (Button) findViewById(R.id.btn_back_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_send_register = (Button) findViewById(R.id.btn_send_register);

        btn_send_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_username_register.getText()) || TextUtils.isEmpty(et_password_register.getText())){
                    Toast.makeText(RegisterActivity.this, "Enter Username and Password", Toast.LENGTH_SHORT).show();
                }
                else if(et_password_register.getText().toString().equals(et_confirm_password_register.getText().toString()))
                { Register();}
                else {
                    Toast.makeText(RegisterActivity.this, "Password confirm not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.spin_kit_reg);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);
    }

    void Register(){
        progressBar.setVisibility(View.VISIBLE);
        BaseUser baseUser=new BaseUser();
//        Toast.makeText(RegisterActivity.this,baseUser.base,Toast.LENGTH_LONG).show();
        final Gson gson=new Gson();
        DangKi dangK=new DangKi();
        okhttp3.Response response=null;
        String data = gson.toJson(new User(et_username_register.getText().toString(),et_password_register.getText().toString()));
        try {
            response=dangK.execute(URL_REGIST,data).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(response.code()==200){Toast.makeText(RegisterActivity.this,"Register Successfully",Toast.LENGTH_LONG).show();finish();}
        else Toast.makeText(RegisterActivity.this,"Register Failed",Toast.LENGTH_LONG).show();
    }

}
