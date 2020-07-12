package com.example.projectbudgetmanager.back_end;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.projectbudgetmanager.MainActivity;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.back_end.controller.DangNhap;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_login, btn_to_register_activity;
    EditText et_username, et_password;
    Animation fromBottom, fromTop;
    ImageView img_logo_login;
    ProgressBar progressBar;
    static String URL_LOGIN = "https://api-backend-nhom15.herokuapp.com/api/login";
    String userNameValue = "" , passwordValue = "";
    boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        img_logo_login = (ImageView) findViewById(R.id.img_logo_login);
        et_username = (EditText) findViewById(R.id.et_username_login);
        et_password = (EditText) findViewById(R.id.et_password_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_to_register_activity = (Button) findViewById(R.id.btn_to_register_activity);
        btn_to_register_activity.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        btn_login.setAnimation(fromBottom);
        btn_to_register_activity.setAnimation(fromBottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        img_logo_login.setAnimation(fromTop);
        et_username.setAnimation(fromTop);
        et_password.setAnimation(fromTop);



        loadDataLogin();

        //Check if login before -> keep login
        if(isLoggedIn){
            //need these code below to load
            final Gson gson = new Gson();
            DangNhap dangNhap = new DangNhap();
            Response response = null;
            JsonObject jsonObject = new JsonObject();
            String redata = "";
            BaseUser baseUser = new BaseUser();
            String data = gson.toJson(new User(userNameValue, passwordValue));

            try {
                response = dangNhap.execute("https://api-backend-nhom15.herokuapp.com/api/login", data).get();
                redata = response.body().string();
                Log.e("aaaaa", "login: try" );
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("aaaaa", "login: exception" );
            }
//            Toast.makeText(this, "" + response.code(), Toast.LENGTH_SHORT).show();
            if (response.code() == 200) {
                jsonObject = gson.fromJson(redata, JsonObject.class);
                //Toast.makeText(LoginActivity.this,jsonObject.get("Token").getAsString(),Toast.LENGTH_LONG).show();
                baseUser.base = jsonObject.get("Token").getAsString();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                //Transfer data get from cloud to mainActivity
                //intent.putExtra("amoutToLimitExpense", jsonObject.get("saving").getAsLong()); //send amountToLimitExpense to main Activity

                //Save data user:
                saveDataLogin();

                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    void login() {
        if (TextUtils.isEmpty(et_username.getText()) || TextUtils.isEmpty(et_password.getText())) {
            Toast.makeText(this, "Enter both Username and Password!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else {
            final Gson gson = new Gson();
            DangNhap dangNhap = new DangNhap();
            Response response = null;
            JsonObject jsonObject = new JsonObject();
            String redata = "";
            BaseUser baseUser = new BaseUser();
            if(isLoggedIn == false){
                userNameValue = et_username.getText().toString();
                passwordValue = et_password.getText().toString();
            }
            String data = gson.toJson(new User(userNameValue, passwordValue));

            try {
                response = dangNhap.execute("https://api-backend-nhom15.herokuapp.com/api/login", data).get();
                redata = response.body().string();
                Log.e("aaaaa", "login: try" );
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("aaaaa", "login: exception" );
            }
//            Toast.makeText(this, "" + response.code(), Toast.LENGTH_SHORT).show();
            if (response.code() == 200) {
                jsonObject = gson.fromJson(redata, JsonObject.class);
                //Toast.makeText(LoginActivity.this,jsonObject.get("Token").getAsString(),Toast.LENGTH_LONG).show();
                baseUser.base = jsonObject.get("Token").getAsString();
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                //Transfer data get from cloud to mainActivity
                //intent.putExtra("amoutToLimitExpense", jsonObject.get("saving").getAsLong()); //send amountToLimitExpense to main Activity

                //Save data user:
                saveDataLogin();

                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                },2500);
                //startActivity(intent);

            } else {
                progressBar.setVisibility(View.GONE);
                jsonObject = gson.fromJson(redata, JsonObject.class);
                Toast.makeText(LoginActivity.this,jsonObject.get("message").getAsString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                progressBar.setVisibility(View.VISIBLE);
                login();
                break;
            case R.id.btn_to_register_activity:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    void saveDataLogin(){
        SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
        editor.putString("username", userNameValue);
        editor.putString("password", passwordValue);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
    void loadDataLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        userNameValue = sharedPreferences.getString("username", "");
        passwordValue = sharedPreferences.getString("password", "");
    }
}

