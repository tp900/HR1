package com.qr.hr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;
import com.qr.hr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qr.hr.utils.DialogFactory.ShowSelectApprove;

public class TiaoXiuActivity extends BaseActivity {
    private TextView tx_name ;
    private  TextView tx_dept;
    private TextView tx_postion;
    private TextView tx_indate;
    private TextView tx_ystartdate;
    private TextView tx_yenddate;
    private TextView tx_startdate;
    private TextView tx_enddate;
    private TextView tx_remark;
    private ArrayAdapter<String> arrayAdapter;
    private Button tx_back;
    private Button tx_submit;
    private EditText tx_approve;
    private String sEmpName ;
    private String sEmpNo ;
    private String sEmpDept ;
    private String sEmpInDate ;
    private String sEmpPostion ;
    private Button tx_selectapprove;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiao_xiu);
        //从缓存读取员工信息
        tx_name = findViewById(R.id.tx_name);
        tx_dept= findViewById(R.id.tx_dept);
        tx_postion = findViewById(R.id.tx_postion);
        tx_indate = findViewById(R.id.tx_indate);
        tx_ystartdate = findViewById(R.id.tx_ystartdate);
        tx_yenddate = findViewById(R.id.tx_yenddate);
        tx_startdate = findViewById(R.id.tx_startdate);
        tx_enddate = findViewById(R.id.tx_enddate);
        tx_back = findViewById(R.id.tx_back);
        tx_submit = findViewById(R.id.tx_submit);
        tx_remark = findViewById(R.id.tx_remark);
        tx_approve = findViewById(R.id.tx_approve);
        tx_approve.setInputType(InputType.TYPE_NULL);
        tx_selectapprove = findViewById(R.id.tx_selectapprove);
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(this);
        sEmpName = editor.getString("emp_name","");
        sEmpNo = editor.getString("emp_no","");
        sEmpDept = editor.getString("emp_dept","");
        sEmpInDate = editor.getString("emp_indate","");
        sEmpPostion = editor.getString("emp_postion","");
        tx_name.setText("姓名: "+sEmpName);
        tx_dept.setText("部门: "+sEmpDept);
        tx_postion.setText("职位: "+sEmpPostion);
        tx_indate.setText("入职日期: "+sEmpInDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        tx_startdate.setText(format.format(date));
        tx_enddate.setText(format.format(date));
        tx_startdate.setInputType(InputType.TYPE_NULL);
        tx_enddate.setInputType(InputType.TYPE_NULL);
        tx_ystartdate.setInputType(InputType.TYPE_NULL);
        tx_yenddate.setInputType(InputType.TYPE_NULL);
        GetDefaultApprove();
        tx_selectapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始
                dialogUtil.ShowProgressDialog1("加载中...",TiaoXiuActivity.this);
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
                                Toast.makeText(TiaoXiuActivity.this,"加载失败,请重试",Toast.LENGTH_LONG).show();
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
                                            alertDialog1 = ShowSelectApprove(list,TiaoXiuActivity.this, new DialogCallBack() {
                                                @Override
                                                public void IsSure(Object obj) {
                                                    Approve approve = (Approve)obj;
                                                    tx_approve.setText(approve.empName);
                                                    tx_approve.setTag(approve.empNo);
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
                            }
                        }catch (Exception ex){}finally {
                            dialogUtil.CloseProgressDialog1();
                        }
                    }
                });
                //结束
            }
        });
        tx_ystartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(TiaoXiuActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        tx_ystartdate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();
            }
        });
        tx_yenddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(TiaoXiuActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        tx_yenddate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();
            }
        });
        tx_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(TiaoXiuActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        tx_startdate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();
            }
        });
        tx_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(TiaoXiuActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        tx_enddate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();
            }
        });

        tx_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(TiaoXiuActivity.this,MainActivity.class);
                startActivity(intent);*/
                TiaoXiuActivity.this.finish();
            }
        });
        tx_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sStartDate = tx_startdate.getText().toString();
                String sEndDate = tx_enddate.getText().toString();
                String sYStartDate = tx_ystartdate.getText().toString();
                String sYEndDate = tx_yenddate.getText().toString();
                String sRemark = tx_remark.getText().toString();
                String sApprove = tx_approve.getTag().toString();
                boolean flg = true;
                if(sStartDate.isEmpty()){
                    tx_startdate.setError("请输入");
                    flg = false;
                }
                if(sEndDate.isEmpty()){
                    tx_enddate.setError("请输入");
                    flg = false;
                }
                if(sYStartDate.isEmpty()){
                    tx_ystartdate.setError("请输入");
                    flg = false;
                }
                if(sYEndDate.isEmpty()){
                    tx_yenddate.setError("请输入");
                    flg = false;
                }

                if(sApprove.isEmpty()){
                    tx_approve.setError("请输入");
                    flg = false;
                }
                if(flg && sStartDate!="" && sEndDate!=""){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try{
                        Date sDate = format.parse(sStartDate);
                        Date eDate = format.parse(sEndDate);
                        if(eDate.compareTo(sDate)<=0){
                             alertDialog = new AlertDialog.Builder(TiaoXiuActivity.this)
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
                if(flg && sYStartDate!="" && sYEndDate!=""){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try{
                        Date sDate = format.parse(sYStartDate);
                        Date eDate = format.parse(sYEndDate);
                        if(eDate.compareTo(sDate)<=0){
                            alertDialog = new AlertDialog.Builder(TiaoXiuActivity.this)
                                    .setMessage("原开始日期必须小于原结束日期")
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
                    dialogUtil.ShowProgressDialog1("正在提交...",TiaoXiuActivity.this);
                    //Utils.ShowProgressDialog("正在提交...",TiaoXiuActivity.this);
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveTX";
                    String url = getResources().getString(R.string.url)+"LeaveTX";
                    RequestBody formBody = new FormBody.Builder()
                            .add("empNo",sEmpNo)
                            .add("startDate",sStartDate)
                            .add("endDate",sEndDate)
                            .add("yStartDate",sYStartDate)
                            .add("yEndDate",sYEndDate)
                            .add("remark",sRemark)
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
                                    Toast.makeText(TiaoXiuActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
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
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String sContext = jsonObject.toString();
                                        final Messages messages = new Gson().fromJson(sContext,Messages.class);
                                        Toast.makeText(TiaoXiuActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                                        if(messages.status==1){
                                            Intent intent = new Intent();
                                            setResult(3,intent);
                                            finish();
                                        }

                                    }catch (Exception ex){
                                        Toast.makeText(TiaoXiuActivity.this,"未知错误",Toast.LENGTH_LONG).show();
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
                                    tx_approve.setText(user.empName);
                                    tx_approve.setTag(user.empNo);
                                }
                            }
                        }catch (Exception ex){}

                    }
                });
            }
        });
    }
}
