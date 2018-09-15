package com.qr.hr;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.Approve;
import com.qr.hr.modles.BuKa;
import com.qr.hr.modles.BuKaD;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.User;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;

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

public class BukaDanActivity extends BaseActivity {
    private EditText bkdate;//补卡日期
    private CheckBox cb_am;//上午补卡
    private EditText am_shour;//上午开始小时
    private EditText am_sminute;//上午开始分钟
    private EditText am_ehour;//上午结束小时
    private EditText am_eminute;//上午结束分钟
    private CheckBox cb_pm;//下午补卡
    private EditText pm_shour;//下午开始小时
    private EditText pm_sminute;//下午开始分钟
    private EditText pm_ehour;//下午结束小时
    private EditText pm_eminute;//下午结束分钟
    private CheckBox cb_jb;//加班补卡
    private EditText jb_shour;//加班开始小时
    private EditText jb_sminute;//加班开始分钟
    private EditText jb_ehour;//加班结束小时
    private EditText jb_eminute;//加班结束分钟
    private CheckBox cb_ld;//漏打卡
    private CheckBox cb_wc;//因公外出
    private CheckBox cb_td;//停电
    private CheckBox cb_tx;//通宵
    private CheckBox cb_qt;//其它
    private EditText approve;//审核人
    private Button selectapprove;//选择审核人
    private EditText yy ;//原因
    private Button bt_submit;//提交按钮
    private String empNo;//工号
    private android.app.AlertDialog alertDialog1;//弹框
    private AlertDialog alertDialog;//弹框
    private BuKa buKa;
    private Button goBack;
    private ProcessDialogUtil dialogUtil=new ProcessDialogUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buka_dan);
        InitController();//初始控件
        GetDefaultApprove();
    }
    //初始化控件
    private void InitController(){
        bkdate = findViewById(R.id.bkdate);//补卡日期
        cb_am = findViewById(R.id.cb_am);//上午补卡
        am_shour = findViewById(R.id.am_shour);//上午开始小时
        am_sminute = findViewById(R.id.am_sminute);//上午开始分钟
        am_ehour = findViewById(R.id.am_ehour);//上午结束小时
        am_eminute = findViewById(R.id.am_eminute);//上午结束分钟
        cb_pm = findViewById(R.id.cb_pm);//下午补卡
        pm_shour = findViewById(R.id.pm_shour);//下午开始小时
        pm_sminute = findViewById(R.id.pm_sminute);//下午开始分钟
        pm_ehour = findViewById(R.id.pm_ehour);//下午结束小时
        pm_eminute = findViewById(R.id.pm_eminute);//下午结束分钟
        cb_jb = findViewById(R.id.cb_jb);//加班补卡
        jb_shour = findViewById(R.id.jb_shour);//加班开始小时
        jb_sminute = findViewById(R.id.jb_sminute);//加班开始分钟
        jb_ehour = findViewById(R.id.jb_ehour);//加班结束小时
        jb_eminute = findViewById(R.id.jb_eminute);//加班结束分钟
        cb_ld = findViewById(R.id.cb_ld);//漏打卡
        cb_wc = findViewById(R.id.cb_wc);//因公外出
        cb_td = findViewById(R.id.cb_td);//停电
        cb_tx = findViewById(R.id.cb_tx);//通宵
        cb_qt = findViewById(R.id.cb_qt);//其它
        approve = findViewById(R.id.approve);//审核人
        selectapprove = findViewById(R.id.selectapprove);//选择审核人
        yy = findViewById(R.id.yy);//补卡原因
        bt_submit = findViewById(R.id.submit);//提交按钮
        goBack = findViewById(R.id.goback);
        bkdate.setInputType(InputType.TYPE_NULL);
        approve.setInputType(InputType.TYPE_NULL);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        bkdate.setText(format.format(new Date()));
        bkdate.setOnClickListener(selectDate);
        bt_submit.setOnClickListener(onSubmit);
        goBack.setOnClickListener(OnGoBack);
        selectapprove.setOnClickListener(selectApprove);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BukaDanActivity.this);
        empNo = preferences.getString("emp_no","");
    }

    //返回事件
    private View.OnClickListener OnGoBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    //日期选择事件
    private View.OnClickListener selectDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerView pvTime = new TimePickerBuilder(BukaDanActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    bkdate.setText(format.format(date));

                }
            }).setCancelText("取消")
                    .setSubmitText("确定")
                    .setType(new boolean[]{true,true,true,true,true,true})
                    .setLabel("年","月","日","时","分","秒")
                    .build();
            pvTime.show();
        }
    };
    //读取默认审核人
    private void GetDefaultApprove(){
        String url = getResources().getString(R.string.url)+"GetDefaultAppover";
        RequestBody body = new FormBody.Builder()
                .add("empno",empNo).build();
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
                                    approve.setText(user.empName);
                                    approve.setTag(user.empNo);
                                }
                            }
                        }catch (Exception ex){}

                    }
                });
            }
        });
    }
    //选择审核人
    private View.OnClickListener selectApprove = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogUtil.ShowProgressDialog1("加载中...",BukaDanActivity.this);
            String url = getResources().getString(R.string.url)+"GetAllAppover";
            RequestBody body = new FormBody.Builder()
                    .add("empno",empNo).build();
            HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(BukaDanActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String sResponse = response.body().string();
                    final List<Approve> list = new ArrayList<>();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                        dialogUtil.CloseProgressDialog1();
                                        alertDialog1 = ShowSelectApprove(list,BukaDanActivity.this, new DialogCallBack() {
                                            @Override
                                            public void IsSure(Object obj) {
                                                Approve appr = (Approve)obj;
                                                approve.setText(appr.empName);
                                                approve.setTag(appr.empNo);
                                                alertDialog1.dismiss();
                                            }

                                            @Override
                                            public void IsCancel() {
                                                alertDialog1.dismiss();
                                            }
                                        });
                                    }
                                }else{
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(BukaDanActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(BukaDanActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                            }finally {
                                dialogUtil.CloseProgressDialog1();
                            }
                        }
                    });

                }
            });
        }
    };
    //提交表单
    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!ValidateData()){
                return;
            }
            if(buKa!=null&&buKa.buKaDetail.size()>0){
                dialogUtil.ShowProgressDialog1("正在提交...",BukaDanActivity.this);
                Gson gson = new Gson();
                String data = gson.toJson(buKa);
                String url = getResources().getString(R.string.url)+"BuKa";
                RequestBody body = new FormBody.Builder()
                        .add("data",data).build();
                HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(BukaDanActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                       final String sResponse = response.body().string();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               try{
                                   JSONObject jsonObject = new JSONObject(sResponse);
                                   String s = jsonObject.toString();
                                   final Messages messages = new Gson().fromJson(s,Messages.class);
                                   dialogUtil.CloseProgressDialog1();
                                   Toast.makeText(BukaDanActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                                   if(messages.status==1){
                                       Intent intent = new Intent();
                                       setResult(3,intent);
                                       finish();
                                   }
                               }catch (Exception ex){
                                   dialogUtil.CloseProgressDialog1();
                                   Toast.makeText(BukaDanActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                               }finally {
                                   dialogUtil.CloseProgressDialog1();
                               }

                           }
                       });
                    }
                });
            }else{
                alertDialog = new AlertDialog.Builder(BukaDanActivity.this)
                        .setTitle("提示")
                        .setMessage("数据有误,请重试")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        }
    };
    ///验证数据
    private boolean ValidateData(){
        boolean flg = true;
        String sBKDate = bkdate.getText().toString();
        boolean bCBAM = cb_am.isChecked();
        String sAMSHour = am_shour.getText().toString();
        String sAMSMinute = am_sminute.getText().toString();
        String sAMEHour = am_ehour.getText().toString();
        String sAMEMinute = am_eminute.getText().toString();
        boolean bCBPM = cb_pm.isChecked();
        String sPMSHour = pm_shour.getText().toString();
        String sPMSMinute = pm_sminute.getText().toString();
        String sPMEHour = pm_ehour.getText().toString();
        String sPMEMinute = pm_eminute.getText().toString();
        boolean bCBJB = cb_jb.isChecked();
        String sJBSHour = jb_shour.getText().toString();
        String sJBSMinute = jb_sminute.getText().toString();
        String sJBEHour = jb_ehour.getText().toString();
        String sJBEMinute = jb_eminute.getText().toString();
        boolean bCBLD = cb_ld.isChecked();
        boolean bCBWC = cb_wc.isChecked();
        boolean bCBTD = cb_td.isChecked();
        boolean bCBTX = cb_tx.isChecked();
        boolean bCBQT = cb_qt.isChecked();
        int dAMSHour=0 ;
        int dAMSMinute=0 ;
        int dAMEHour=0 ;
        int dAMEMinute=0;
        int dPMSHour=0 ;
        int dPMSMinute=0 ;
        int dPMEHour=0 ;
        int dPMEMinute=0;
        int dJBSHour=0 ;
        int dJBSMinute=0 ;
        int dJBEHour =0;
        int dJBEMinute =0;
        String sApprove = approve.getTag().toString();
        String sYY = yy.getText().toString().isEmpty()?"":yy.getText().toString();//补卡原因
        String sReason = ""; //补卡类型
        if(flg&&bCBAM){
            if(sAMEHour.isEmpty()){
                flg= false;
                am_shour.setError("请输入");
            }
            if(sAMEMinute.isEmpty()){
                flg = false;
                am_sminute.setError("请输入");
            }
            if(sAMEHour.isEmpty()){
                flg = false;
                am_ehour.setError("请输入");
            }
            if(sAMEMinute.isEmpty()){
                flg = false;
                am_eminute.setError("请输入");
            }
            if(flg){
                try{
                     dAMSHour = Integer.parseInt(sAMSHour);
                     dAMSMinute = Integer.parseInt(sAMSMinute);
                     dAMEHour = Integer.parseInt(sAMEHour);
                     dAMEMinute = Integer.parseInt(sAMEMinute);
                    if(dAMSHour>12 || dAMSHour<0){
                        flg = false;
                        am_shour.setError("请输入0-12之间数");
                    }
                    if(dAMSMinute>=60 ||dAMSMinute<0){
                        flg = false;
                        am_sminute.setError("请输入0-59之间的数");
                    }
                    if(dAMEHour>12||dAMEHour<0){
                        flg = false;
                        am_ehour.setError("请输入0-12之间的数");
                    }
                    if(dAMEMinute>60 || dAMEMinute<0){
                        flg = false;
                        am_eminute.setError("请输入0-59之间的数");
                    }
                    if(flg){
                        if(dAMEHour<=dAMSHour){
                            flg = false;
                            am_ehour.setError("不能小于从时间");
                        }
                    }
                }catch (Exception ex){
                    flg = false;
                }


            }
        }
        if(flg&&bCBPM){
            if(sPMEHour.isEmpty()){
                flg= false;
                pm_shour.setError("请输入");
            }
            if(sPMEMinute.isEmpty()){
                flg = false;
                pm_sminute.setError("请输入");
            }
            if(sPMEHour.isEmpty()){
                flg = false;
                pm_ehour.setError("请输入");
            }
            if(sPMEMinute.isEmpty()){
                flg = false;
                pm_eminute.setError("请输入");
            }
            if(flg){
                try{
                     dPMSHour = Integer.parseInt(sPMSHour);
                     dPMSMinute = Integer.parseInt(sPMSMinute);
                     dPMEHour = Integer.parseInt(sPMEHour);
                     dPMEMinute = Integer.parseInt(sPMEMinute);
                    if(dPMSHour>23 || dPMSHour<12){
                        flg = false;
                        pm_shour.setError("请输入12-23之间数");
                    }
                    if(dPMSMinute>=60 ||dPMSMinute<0){
                        flg = false;
                        pm_sminute.setError("请输入0-59之间的数");
                    }
                    if(dPMEHour>23||dPMEHour<12){
                        flg = false;
                        pm_ehour.setError("请输入12-23之间的数");
                    }
                    if(dPMEMinute>60 || dPMEMinute<0){
                        flg = false;
                        pm_eminute.setError("请输入0-59之间的数");
                    }
                    if(flg){
                        if(dPMEHour<=dPMSHour){
                            flg = false;
                            pm_ehour.setError("不能小于从时间");
                        }
                    }
                }catch (Exception ex){
                    flg = false;
                }


            }
        }
        if(flg&&bCBJB){
            if(sJBEHour.isEmpty()){
                flg= false;
                pm_shour.setError("请输入");
            }
            if(sJBEMinute.isEmpty()){
                flg = false;
                pm_sminute.setError("请输入");
            }
            if(sJBEHour.isEmpty()){
                flg = false;
                pm_ehour.setError("请输入");
            }
            if(sJBEMinute.isEmpty()){
                flg = false;
                pm_eminute.setError("请输入");
            }
            if(flg){
                try{
                     dJBSHour = Integer.parseInt(sJBSHour);
                     dJBSMinute = Integer.parseInt(sJBSMinute);
                     dJBEHour = Integer.parseInt(sJBEHour);
                     dJBEMinute = Integer.parseInt(sJBEMinute);
                    if(dJBSHour>23 || dJBSHour<12){
                        flg = false;
                        jb_shour.setError("请输入12-23之间数");
                    }
                    if(dJBSMinute>=60 ||dJBSMinute<0){
                        flg = false;
                        jb_sminute.setError("请输入0-59之间的数");
                    }
                    if(dJBEHour>23||dJBEHour<12){
                        flg = false;
                        jb_ehour.setError("请输入12-23之间的数");
                    }
                    if(dJBEMinute>60 || dJBEMinute<0){
                        flg = false;
                        jb_eminute.setError("请输入0-59之间的数");
                    }
                    if(flg){
                        if(dJBEHour<=dJBSHour){
                            flg = false;
                            jb_ehour.setError("不能小于从时间");
                        }
                    }
                }catch (Exception ex){
                    flg = false;
                }


            }
        }
        if(flg){
            if(!bCBAM&&!bCBPM&&!bCBJB){
                flg=false;
                 alertDialog = new AlertDialog.Builder(BukaDanActivity.this)
                        .setTitle("提示")
                        .setMessage("请输入补卡时间")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        })
                        .create();
                 alertDialog.show();

            }
        }
        if(flg){
            if(!bCBLD&&!bCBWC&&!bCBTD&&!bCBTX&&!bCBQT){
                flg=false;
                alertDialog = new AlertDialog.Builder(BukaDanActivity.this)
                        .setTitle("提示")
                        .setMessage("请选择补卡类型")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        }
        if(flg){
            if(sApprove.isEmpty()){
                flg = false;
                alertDialog = new AlertDialog.Builder(BukaDanActivity.this)
                        .setTitle("提示")
                        .setMessage("请选择审核人")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        }
        if(bCBLD){
            sReason +="漏打卡,";
        }
        if(bCBWC){
            sReason+="因公外出,";
        }
        if(bCBTD){
            sReason+="停电,";
        }
        if(bCBTX){
            sReason+="调休,";
        }
        if(bCBQT){
            sReason+="其它,";
        }
        if(flg){
            buKa = new BuKa();
            buKa.empNo = this.empNo;
            buKa.approve = sApprove;
            buKa.buKaDate = sBKDate;
            buKa.reason = sReason+sYY;
            buKa.status="待审核";
            buKa.buKaDetail = new ArrayList<>();
            buKa.id=0;
            if(bCBAM){
                BuKaD buKaD = new BuKaD();
                buKaD.buKaId=0;
                buKaD.buKaType="上午";
                buKaD.fromHour=dAMSHour;
                buKaD.fromMinute=dAMSMinute;
                buKaD.toHour = dAMEHour;
                buKaD.toMinute = dAMEMinute;
                buKa.buKaDetail.add(buKaD);
            }
            if(bCBPM){
                BuKaD buKaD = new BuKaD();
                buKaD.buKaId=0;
                buKaD.buKaType="下午";
                buKaD.fromHour=dPMSHour;
                buKaD.fromMinute=dPMSMinute;
                buKaD.toHour = dPMEHour;
                buKaD.toMinute = dPMEMinute;
                buKa.buKaDetail.add(buKaD);
            }
            if(bCBJB){
                BuKaD buKaD = new BuKaD();
                buKaD.buKaId=0;
                buKaD.buKaType="加班";
                buKaD.fromHour=dJBSHour;
                buKaD.fromMinute=dJBSMinute;
                buKaD.toHour = dJBEHour;
                buKaD.toMinute = dJBEMinute;
                buKa.buKaDetail.add(buKaD);
            }
        }
        return flg;
    }
}
