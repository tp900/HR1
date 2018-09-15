package com.qr.hr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qr.hr.utils.ProcessDialogUtil;
import com.qr.hr.utils.Utils;

import com.google.gson.Gson;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.User;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class LoginActivity extends BaseActivity {
    private EditText textUserName;
    private EditText textPWD;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    //private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textUserName = findViewById(R.id.username);
        textPWD = findViewById(R.id.pwd);
        Button  button = findViewById(R.id.bt_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utils.ShowProgressDialog("正在登录...",LoginActivity.this);
                dialogUtil.ShowProgressDialog1("正在登录...",LoginActivity.this);
                String sUserName = textUserName.getText().toString();
                String sPWD = textPWD.getText().toString();
                Boolean flg = true;
                if(sUserName.isEmpty()){
                    flg = false;
                    ValidUserName();
                }
                if(sPWD.isEmpty()){
                    flg = false;
                    ValidPWD();
                }
                if(flg){

                    //String url = "http://192.168.22.17/Services/API.asmx/Login";
                    String url = getResources().getString(R.string.url)+"Login";
                    RequestBody formBody = new FormBody.Builder()
                            .add("username",sUserName)
                            .add("pwd",sPWD)
                            .build();
                    HttpUtil.SendOkHttpRequest(url, formBody, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException ex) {
                          final String s = ex.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(LoginActivity.this,"服务器连接失败,请重试",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                          final  Response rsp = response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HandleLogin(rsp);
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                }
                            });

                        }
                    });
                }
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String emp_no = preferences.getString("emp_no","");
        //有缓存时直接进入主页
        if(!emp_no.isEmpty()){
            Intent intent = new Intent(this,MainActivity.class);
            //intent.putExtra("user",user);
            startActivity(intent);
            //Utils.CloseProgressDialog();
            dialogUtil.CloseProgressDialog1();
            LoginActivity.this.finish();
        }
    }
    private void ValidUserName(){
        textUserName.setError("不能为空");
    }
    private void ValidPWD(){
        textPWD.setError("不能为空");
    }
    private void HandleLogin(Response response){
        try{
            String sResponse = response.body().string();
            if(!sResponse.isEmpty()){
                JSONArray jsonArray = new JSONArray(sResponse);
                if(null!=jsonArray&&jsonArray.length()>0){
                    String mContent = jsonArray.getJSONObject(0).toString();
                    String uContent = jsonArray.getJSONObject(1).toString();
                    Messages messages = new Gson().fromJson(mContent,Messages.class);
                    User user = new Gson().fromJson(uContent,User.class);
                    if(null==messages||null==user){
                        Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
                    }else{
                        if(messages.status!= 1){
                            Toast.makeText(this,"登录失败"+messages.msg,Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                            editor.putString("emp_no",user.getEmpNo());
                            editor.putString("emp_dept",user.getDept());
                            editor.putString("emp_sex",user.getSex());
                            editor.putString("emp_name",user.getEmpName());
                            editor.putString("emp_postion",user.getPostion());
                            editor.putString("emp_indate",user.getInDate());
                            editor.putString("emp_photo",user.getPhoto());
                            editor.apply();
                            Intent intent = new Intent(this,MainActivity.class);
                            //intent.putExtra("user",user);
                            startActivity(intent);
                            //Utils.CloseProgressDialog();
                            dialogUtil.CloseProgressDialog1();
                            LoginActivity.this.finish();
                        }
                    }
                }else{
                    Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
            }

        }catch (IOException ex){
            Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
        }catch (JSONException jex){
            Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
        }finally {
            dialogUtil.CloseProgressDialog1();
        }

    }

}
