package com.qr.hr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.Approve;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.User;
import com.qr.hr.utils.DialogFactory;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;
import com.qr.hr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static com.qr.hr.utils.DialogFactory.ShowSelectApprove;

public class QingJiaActivity extends BaseActivity {
    private TextView qj_name ;
    private  TextView qj_dept;
    private TextView qj_postion;
    private TextView qj_indate;
    private TextView qj_startdate;
    private TextView qj_enddate;
    private Spinner qj_type;
    private TextView qj_days;
    private TextView qj_hours;
    private TextView qj_remark;
    private List<String> jiaBieList= new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Button qj_back;
    private Button qj_submit;
    private String sEmpName ;
    private String sEmpNo ;
    private String sEmpDept ;
    private String sEmpInDate ;
    private String sEmpPostion ;
    private String sType;
    private EditText qj_approve;
    private Button selectApprove;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qing_jia);
        //从缓存读取员工信息
        qj_name = findViewById(R.id.qj_name);
        qj_dept= findViewById(R.id.qj_dept);
        qj_postion = findViewById(R.id.qj_postion);
        qj_indate = findViewById(R.id.qj_indate);
        qj_startdate = findViewById(R.id.qj_startdate);
        qj_enddate = findViewById(R.id.qj_enddate);
        qj_type = findViewById(R.id.qj_type);
        qj_back = findViewById(R.id.qj_back);
        qj_submit = findViewById(R.id.qj_submit);
        qj_days = findViewById(R.id.qj_days);
        qj_hours = findViewById(R.id.qj_hous);
        qj_remark = findViewById(R.id.qj_remark);
        qj_approve = findViewById(R.id.qj_approve);
        selectApprove = findViewById(R.id.qj_selectapprove);
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(this);
         sEmpName = editor.getString("emp_name","");
         sEmpNo = editor.getString("emp_no","");
         sEmpDept = editor.getString("emp_dept","");
         sEmpInDate = editor.getString("emp_indate","");
         sEmpPostion = editor.getString("emp_postion","");
        qj_name.setText("姓名: "+sEmpName);
        qj_dept.setText("部门: "+sEmpDept);
        qj_postion.setText("职位: "+sEmpPostion);
        qj_indate.setText("入职日期: "+sEmpInDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        qj_startdate.setText(format.format(date));
        qj_enddate.setText(format.format(date));
        SetJiaBie();
        qj_startdate.setInputType(InputType.TYPE_NULL);
        qj_enddate.setInputType(InputType.TYPE_NULL);
        qj_approve.setInputType(InputType.TYPE_NULL);
        GetDefaultApprove();
        selectApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtil.ShowProgressDialog1("加载中...",QingJiaActivity.this);
                String url = getResources().getString(R.string.url)+"GetAllAppover";
                RequestBody body = new FormBody.Builder()
                        .add("empno",sEmpNo).build();
                HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(QingJiaActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String sResponse = response.body().string();
                        final List<Approve> list = new ArrayList<>();
                        try{
                            JSONArray jsonArray = new JSONArray(sResponse);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String s = jsonObject.toString();
                            Messages messages = new Gson().fromJson(s,Messages.class);
                            if(messages.status ==1){
                                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                if(null!=jsonArray1&&jsonArray1.length()>0){
                                    list.clear();
                                    for(int i = 0;i<jsonArray1.length();i++){
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        String s1 = jsonObject1.toString();
                                        User user = new Gson().fromJson(s1,User.class);
                                        Approve approve = new Approve();
                                        approve.empName = user.empName;
                                        approve.empNo = user .empNo;
                                        approve.postion = user.postion;
                                        list.add(approve);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //DialogFactory factory = new DialogFactory();
                                            dialogUtil.CloseProgressDialog1();
                                            alertDialog1 = ShowSelectApprove(list,QingJiaActivity.this, new DialogCallBack() {
                                                @Override
                                                public void IsSure(Object obj) {
                                                   Approve approve = (Approve)obj;
                                                    qj_approve.setText(approve.empName);
                                                    qj_approve.setTag(approve.empNo);
                                                    alertDialog1.dismiss();
                                                }

                                                @Override
                                                public void IsCancel() {
                                                    alertDialog1.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            }else{
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(QingJiaActivity.this,"加载失败,请重试",Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(QingJiaActivity.this,"未知错误,请重试",Toast.LENGTH_LONG).show();
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                        }
                    }
                });


            }
        });
       qj_startdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               TimePickerView pvTime = new TimePickerBuilder(QingJiaActivity.this, new OnTimeSelectListener() {
                   @Override
                   public void onTimeSelect(Date date, View v) {
                       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                       qj_startdate.setText(format.format(date));

                   }
               }).setCancelText("取消")
                       .setSubmitText("确定")
                       .setType(new boolean[]{true,true,true,true,true,true})
                       .setLabel("年","月","日","时","分","秒")
                       .build();
               pvTime.show();
           }
       });
        qj_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(QingJiaActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        qj_enddate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();
            }
        });
        qj_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qj_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sType = jiaBieList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sType = null;
            }
        });
        qj_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sStartDate = qj_startdate.getText().toString();
                String sEndDate = qj_enddate.getText().toString();
                String sDays = qj_days.getText().toString();
                String sHours = qj_hours.getText().toString();
                String sRemark = qj_remark.getText().toString();
                String sApprove = qj_approve.getTag().toString();
                boolean flg = true;
                if(sStartDate.isEmpty()){
                    qj_startdate.setError("请输入");
                    flg = false;
                }
                if(sEndDate.isEmpty()){
                    qj_enddate.setError("请输入");
                    flg = false;
                }
                if(sDays.isEmpty()){
                    qj_days.setError("请输入");
                    flg = false;
                }
                if(sHours.isEmpty()){
                    qj_hours.setError("请输入");
                    flg = false;
                }
                if(sApprove.isEmpty()){
                    qj_approve.setError("请输入");
                    flg = false;
                }
                if(sType.isEmpty()){
                    alertDialog = new AlertDialog.Builder(QingJiaActivity.this)
                            .setMessage("请选择假别")
                            .setTitle("提示")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog.show();
                    flg = false;
                }

                if(flg && sDays!=""&&sHours!=""){
                    double dDays = Double.parseDouble(sDays);
                    double dHours = Double.parseDouble(sHours);
                    if(dDays<=0 && dHours<=0){
                        alertDialog = new AlertDialog.Builder(QingJiaActivity.this)
                                .setMessage("总天数与小时数不能同时等于0")
                                .setTitle("提示")
                                .setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                })
                                .create();
                        alertDialog.show();
                        flg = false;
                    }
                }
                if(flg && sStartDate!="" && sEndDate!=""){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try{
                        Date sDate = format.parse(sStartDate);
                        Date eDate = format.parse(sEndDate);
                        if(eDate.compareTo(sDate)<=0){
                            alertDialog = new AlertDialog.Builder(QingJiaActivity.this)
                                    .setMessage("开始日期必须小于结束日期")
                                    .setTitle("提示")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.dismiss();
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                            flg = false;
                        }
                    }catch (Exception ex){
                        flg = false;
                    }

                }
                if(!flg){
                    return;
                }else{
                    //开始提交数据
                    dialogUtil.ShowProgressDialog1("正在提交...",QingJiaActivity.this);
                    //Utils.ShowProgressDialog("正在提交...",QingJiaActivity.this);
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveQJ";
                    String url = getResources().getString(R.string.url)+"LeaveQJ";
                    RequestBody formBody = new FormBody.Builder()
                            .add("empNo",sEmpNo)
                            .add("startDate",sStartDate)
                            .add("endDate",sEndDate)
                            .add("days",sDays)
                            .add("hours",sHours)
                            .add("remark",sRemark)
                            .add("qjtype",sType)
                            .add("approve",sApprove)
                            .build();
                    HttpUtil.SendOkHttpRequest(url, formBody, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(QingJiaActivity.this,"服务器连接失败",Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                          final  String sResponse = response.body().string();
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  try{
                                      //Utils.CloseProgressDialog();
                                      JSONObject jsonObject = new JSONObject(sResponse);
                                      String sContext = jsonObject.toString();
                                      final Messages messages = new Gson().fromJson(sContext,Messages.class);
                                      dialogUtil.CloseProgressDialog1();
                                      Toast.makeText(QingJiaActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                                      if(messages.status==1){
                                          Intent intent = new Intent();
                                          setResult(3,intent);
                                          finish();
                                      }
                                  }catch (Exception ex){
                                      dialogUtil.CloseProgressDialog1();
                                      Toast.makeText(QingJiaActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                                  }finally {
                                      dialogUtil.CloseProgressDialog1();
                                  }

                              }
                          });
                        }
                    });
                }
            }
        });



    }
    private void GetDefaultApprove(){
        String url = getResources().getString(R.string.url)+"GetDefaultAppover";
        RequestBody body = new FormBody.Builder()
                .add("empno",sEmpNo).build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String sResponse = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray jsonArray = new JSONArray(sResponse);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String s = jsonObject.toString();
                            Messages messages = new Gson().fromJson(s,Messages.class);
                            if(messages.status==1){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(1);
                                String s1 = jsonObject1.toString();
                                User user = new Gson().fromJson(s1,User.class);
                                if(user!=null){
                                    qj_approve.setText(user.empName);
                                    qj_approve.setTag(user.empNo);
                                }
                            }
                        }catch (Exception ex){}

                    }
                });
            }
        });
    }
    private void SetJiaBie(){
        //String url = "http://192.168.22.17/Services/API.asmx/GetLeave";
        String url = getResources().getString(R.string.url)+"GetLeave";
        HttpUtil.SendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("11", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String sResponse = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray jsonArray = new JSONArray(sResponse);
                            if(jsonArray!=null && jsonArray.length()>0){
                                Messages messages = new Gson().fromJson(jsonArray.getJSONObject(0).toString(),Messages.class);
                                if(messages.status==1){
                                    JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                    if(jsonArray1!=null && jsonArray1.length()>0){
                                        for(int i = 0;i<jsonArray1.length();i++){
                                            String jiabie = jsonArray1.getJSONObject(i).getString("jiabei");
                                            jiaBieList.add(jiabie);
                                        }
                                        arrayAdapter = new ArrayAdapter<>(QingJiaActivity.this,android.R.layout.simple_spinner_dropdown_item,jiaBieList);
                                        qj_type.setAdapter(arrayAdapter);
                                    }
                                }
                            }
                        }catch (Exception ex){
                            Log.d("1", ex.getMessage());
                        }

                    }
                });
            }
        });
    }
}
