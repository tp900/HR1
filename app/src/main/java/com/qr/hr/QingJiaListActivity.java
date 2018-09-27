package com.qr.hr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.Leave;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.QiangJiaDan;
import com.qr.hr.swipe.Menu;
import com.qr.hr.swipe.MenuCreator;
import com.qr.hr.swipe.MenuItem;
import com.qr.hr.swipe.MyListView;
import com.qr.hr.utils.DialogFactory;
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

public class QingJiaListActivity extends BaseActivity {
    private List<QiangJiaDan> leaves =new ArrayList<>();
    private MyListView swipeMenuListView;
    private Button bt_back;
    private Button bt_add;
    private QiangJiaDan cLeave;
    private int cPostion;
    private String empNo;
    private QiangJiaListAdapter appAdapter;
    private AlertDialog alertDialog;
    private Button bt_find;
    private QingJiaFilter qingJiaFilter;
    private LinearLayout qj_filter;
    private CheckBox juejue;
    private CheckBox daishen;
    private CheckBox wanjie;
    private EditText sDate;
    private EditText eDate;
    private Button bt_findResult;
    private Button bt_cancel;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    private SwipeRefreshLayout swipeRefreshLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qing_jia_list);
        Utils.CloseProgressDialog();
        swipeMenuListView = findViewById(R.id.qjlist);
        bt_back = findViewById(R.id.qjlist_back);
        bt_add = findViewById(R.id.qjlist_add);
        bt_find = findViewById(R.id.qjlist_find);
        qj_filter = findViewById(R.id.qj_filter);
        qj_filter.setVisibility(View.GONE);
        juejue = findViewById(R.id.juejue);
        daishen = findViewById(R.id.daishen);
        wanjie = findViewById(R.id.wanjie);
        sDate = findViewById(R.id.qj_sdatefilter);
        eDate = findViewById(R.id.qj_edatefilter);
        bt_findResult = findViewById(R.id.bt_findfilter);
        bt_cancel = findViewById(R.id.bt_closefilter);
        sDate.setInputType(InputType.TYPE_NULL);
        eDate.setInputType(InputType.TYPE_NULL);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        empNo = sharedPreferences.getString("emp_no","");
        InitQiangJiaList(empNo);
        InitAdapter();
        swipeMenuListView.setAdapter(appAdapter);

        bt_back.setOnClickListener(GoBack);
        bt_add.setOnClickListener(Add);
        bt_find.setOnClickListener(Find);
        sDate.setOnClickListener(SelectStartDate);
        eDate.setOnClickListener(SelectEndDate);
        bt_findResult.setOnClickListener(FindResult);
        bt_cancel.setOnClickListener(Cancel);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        sDate.setText(format.format(new Date()));
        eDate.setText(format.format(new Date()));
        swipeRefreshLayout = findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InitQiangJiaList(empNo);
            }
        });
    }

    private void InitAdapter(){
        //设置滑动菜单
        final MenuCreator menuCreator = new MenuCreator() {
            @Override
            public void CreateMenu(Menu menu) {
                if(menu.getMenuType()==0){
                    createMenu(menu);
                }
            }
            private void createMenu(Menu menu) {
                MenuItem item1 = new MenuItem(QingJiaListActivity.this);
                item1.setBackground(R.color.colorRefuse);
                item1.setWidth(dp2px(50));
                item1.setTitle("删除");
                item1.setTitleColor(Color.WHITE);
                item1.setTitleSize(18);
                item1.setId(0);
                menu.menuItems.add(item1);
            }
        };
        appAdapter = new QiangJiaListAdapter(leaves,QingJiaListActivity.this){
            @Override
            public void CreateMenu(Menu menu) {
                menuCreator.CreateMenu(menu);
            }
        };

    };

    //返回按钮
    private View.OnClickListener GoBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            QingJiaListActivity.this.finish();
        }
    };
    //新增按钮
    private View.OnClickListener Add = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(QingJiaListActivity.this,QingJiaActivity.class);
            startActivityForResult(intent,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==3){
            //新增成功返回刷新数据
            InitQiangJiaList(empNo);
        }
    }

    //查询按钮
    private View.OnClickListener Find = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             qj_filter.setVisibility(View.VISIBLE);
        }
    };
    //查询结果
    private View.OnClickListener FindResult = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Utils.ShowProgressDialog("查询中...",QingJiaListActivity.this);
            dialogUtil.ShowProgressDialog1("查询中...",QingJiaListActivity.this);
            String sWanjie = wanjie.isChecked()?"1":"0";
            String sDaishen = daishen.isChecked()?"1":"0";
            String sJuejue = juejue.isChecked()?"1":"0";
            String sStartDate = sDate.getText().toString();
            String sEndDate = eDate.getText().toString();
            if(empNo.isEmpty()){
                //Utils.CloseProgressDialog();
                dialogUtil.CloseProgressDialog1();
                return;
            }
            RequestBody body = new FormBody.Builder()
                    .add("empno",empNo)
                    .add("wanjie",sWanjie)
                    .add("juejue",sJuejue)
                    .add("daishen",sDaishen)
                    .add("sdate",sStartDate)
                    .add("edate",sEndDate)
                    .build();
            String url = getResources().getString(R.string.url)+"FindLeave";
            HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Utils.CloseProgressDialog();
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(QingJiaListActivity.this,"服务器连接失败,请重试",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String sResponse = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Utils.CloseProgressDialog();
                            try {
                                leaves.clear();
                                JSONArray jsonArray = new JSONArray(sResponse);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String jsonStr = jsonObject.toString();
                                Messages messages = new Gson().fromJson(jsonStr,Messages.class);
                                if(messages.status==1){
                                    JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                    if(null!=jsonArray1&&jsonArray1.length()>0){

                                        for(int i = 0;i<jsonArray1.length();i++){
                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                            String jsonStr1 = jsonObject1.toString();
                                            QiangJiaDan dan = new Gson().fromJson(jsonStr1,QiangJiaDan.class);
                                            leaves.add(dan);
                                        }
                                        qj_filter.setVisibility(View.GONE);

                                    }
                                }
                                appAdapter.notifyDataSetChanged();
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(QingJiaListActivity.this,messages.msg,Toast.LENGTH_SHORT).show();
                            }catch (Exception ex){
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(QingJiaListActivity.this,"未知错误",Toast.LENGTH_SHORT);
                            }finally {
                                dialogUtil.CloseProgressDialog1();
                            }

                        }
                    });

                }
            });
        }
    };
    //取消查询
    private View.OnClickListener Cancel=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            qj_filter.setVisibility(View.GONE);
        }
    };
    //选择开始日期
    private View.OnClickListener SelectStartDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerView pvTime = new TimePickerBuilder(QingJiaListActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    sDate.setText(format.format(date));

                }
            }).setCancelText("取消")
                    .setSubmitText("确定")
                    .setType(new boolean[]{true,true,true,true,true,true})
                    .setLabel("年","月","日","时","分","秒")
                    .build();
            pvTime.show();
        }
    };
    //选择结束日期
    private View.OnClickListener SelectEndDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerView pvTime = new TimePickerBuilder(QingJiaListActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    eDate.setText(format.format(date));

                }
            }).setCancelText("取消")
                    .setSubmitText("确定")
                    .setType(new boolean[]{true,true,true,true,true,true})
                    .setLabel("年","月","日","时","分","秒")
                    .build();
            pvTime.show();
        }
    };

    private void InitQiangJiaList(String empNo){
        if(empNo.isEmpty()){
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        dialogUtil.ShowProgressDialog1("加载中...",QingJiaListActivity.this);
        RequestBody body = new FormBody.Builder()
                .add("empno",empNo).build();
        String url = getResources().getString(R.string.url)+"QiangJiaList";
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Utils.CloseProgressDialog();
                        dialogUtil.CloseProgressDialog1();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(QingJiaListActivity.this,"服务器连接失败,请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String sResponse = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            leaves.clear();
                            JSONArray jsonArray = new JSONArray(sResponse);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String jsonStr = jsonObject.toString();
                            Messages messages = new Gson().fromJson(jsonStr,Messages.class);
                            if(messages.status==1){
                                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                if(null!=jsonArray1&&jsonArray1.length()>0){

                                    for(int i = 0;i<jsonArray1.length();i++){
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        String jsonStr1 = jsonObject1.toString();
                                        QiangJiaDan dan = new Gson().fromJson(jsonStr1,QiangJiaDan.class);
                                        leaves.add(dan);
                                    }

                                }
                            }else{
                                dialogUtil.CloseProgressDialog1();
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(QingJiaListActivity.this,messages.msg,Toast.LENGTH_SHORT).show();
                            }
                            appAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(QingJiaListActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                QingJiaListActivity.this.getResources().getDisplayMetrics());
    }
}
