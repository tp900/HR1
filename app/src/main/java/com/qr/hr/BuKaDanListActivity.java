package com.qr.hr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuView;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.BuKa;
import com.qr.hr.modles.BuKaD;
import com.qr.hr.modles.BuKaDanItem;
import com.qr.hr.modles.Messages;
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
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuKaDanListActivity extends BaseActivity {
    private Button bt_Back;
    private Button bt_Find;
    private Button bt_Add;
    private LinearLayout layout_Filter;
    private CheckBox cb_DaiShen;
    private CheckBox cb_WanJie;
    private CheckBox cb_JueJue;
    private EditText et_SDate;
    private EditText et_EDate;
    private Button bt_Submit;
    private Button bt_Cancel;
    private SwipeMenuListView listView;
    private SwipeMenuCreator creator;
    private List<BuKaDanItem> buKaList = new ArrayList<>();
    private BuKa currentBuKa;
    private int CurrentPostion;
    private BuKaDanAdapter buKaDanAdapter;
    private String empNo;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.CloseProgressDialog();
        setContentView(R.layout.activity_bukadanlist);
        InitControllers();//初始化控件
        BindEvent();//控件绑定事件

    }
    //初始控件
    private void InitControllers(){
        bt_Back = findViewById(R.id.back);
        bt_Find = findViewById(R.id.find);
        bt_Add = findViewById(R.id.add);
        layout_Filter = findViewById(R.id.filter);
        layout_Filter.setVisibility(View.GONE);
        cb_DaiShen = findViewById(R.id.daishen);
        cb_WanJie = findViewById(R.id.juejue);
        cb_JueJue = findViewById(R.id.wanjie);
        et_SDate = findViewById(R.id.sdate);
        et_EDate = findViewById(R.id.edate);
        et_SDate.setInputType(InputType.TYPE_NULL);
        et_EDate.setInputType(InputType.TYPE_NULL);
        bt_Submit = findViewById(R.id.submit);
        bt_Cancel=findViewById(R.id.cancel);
        listView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InitBuKaDanList();
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BuKaDanListActivity.this);
        empNo = preferences.getString("emp_no","");

        buKaDanAdapter = new BuKaDanAdapter(buKaList,empNo,BuKaDanListActivity.this);
        listView.setAdapter(buKaDanAdapter);
        InitCreator();
        listView.setMenuCreator(creator);
        InitBuKaDanList();
    }
    private void BindEvent(){
        bt_Back.setOnClickListener(GoBack);
        bt_Find.setOnClickListener(GoFind);
        bt_Add.setOnClickListener(GoAdd);
        et_SDate.setOnClickListener(SelectSDate);
        et_EDate.setOnClickListener(SelectEDate);
        bt_Submit.setOnClickListener(OnSubmit);
        bt_Cancel.setOnClickListener(CancelFind);
        listView.setOnMenuItemClickListener(OnMenuClick);
    }

    //加载近三月补卡单
    private void InitBuKaDanList(){
        dialogUtil.ShowProgressDialog1("正在加载...",BuKaDanListActivity.this);
        String url = getResources().getString(R.string.url)+"GetBuKaDanList";
        RequestBody body = new FormBody.Builder().add("empno",empNo).build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.CloseProgressDialog1();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(BuKaDanListActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG);
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
                            buKaList.clear();
                            JSONArray jsonArray = new JSONArray(sResponse);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String s = jsonObject.toString();
                            Messages messages = new Gson().fromJson(s,Messages.class);
                            if(messages.status==1){
                                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                if(jsonArray1!=null&&jsonArray1.length()>0){

                                    for(int i = 0;i<jsonArray1.length();i++){

                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        String s1 = jsonObject1.toString();
                                        BuKaDanItem item = new Gson().fromJson(s1,BuKaDanItem.class);
                                        buKaList.add(item);
                                    }

                                }
                            }
                            buKaDanAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(BuKaDanListActivity.this,"未知错误",Toast.LENGTH_LONG);
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

    }
    //设置滑动菜单
    private void InitCreator(){
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                if(menu.getViewType()==0){
                    createMenu2(menu);
                }

            }
            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(R.color.colorRefuse);
                item1.setWidth(dp2px(90));
                item1.setTitle("删除");
                item1.setTitleColor(Color.WHITE);
                item1.setTitleSize(18);
                menu.addMenuItem(item1);
            }
        };
    }
    //设置滑动菜单点击事件
    private SwipeMenuListView.OnMenuItemClickListener OnMenuClick = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final int position, final SwipeMenu menu, int index) {
            dialogUtil.ShowAlertDialog("确定删除?", BuKaDanListActivity.this, new DialogCallBack() {
                @Override
                public void IsSure(Object obj) {
                    BuKaDanItem item = buKaList.get(position);
                    dialogUtil.ShowProgressDialog1("处理中...",BuKaDanListActivity.this);
                    String url = getResources().getString(R.string.url)+"CancelBuKa";
                    RequestBody body = new FormBody.Builder().add("empno",item.approve).add("id",item.id+"").build();
                    HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(BuKaDanListActivity.this,"删除失败,请重试",Toast.LENGTH_LONG).show();
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
                                        Messages messages = new Gson().fromJson(s,Messages.class);

                                        if(messages.status==1){
                                            buKaList.remove(position);
                                            buKaDanAdapter.notifyDataSetChanged();
                                        }
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(BuKaDanListActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(BuKaDanListActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                                    }finally {
                                        dialogUtil.CloseProgressDialog1();
                                    }
                                }
                            });
                        }
                    });


                }

                @Override
                public void IsCancel() {

                }
            });
            return false;
        }
    };
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    //返回事件
    private View.OnClickListener GoBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BuKaDanListActivity.this.finish();
        }
    };
    //查询事件
    private View.OnClickListener GoFind = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            layout_Filter.setVisibility(View.VISIBLE);
        }
    };
    //新增事件
    private View.OnClickListener GoAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BuKaDanListActivity.this,BukaDanActivity.class);
            startActivityForResult(intent,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==3){
            //返回刷新数据
            InitBuKaDanList();
        }
    }

    //选择开始日期
    private View.OnClickListener SelectSDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerView pvTime = new TimePickerBuilder(BuKaDanListActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    et_SDate.setText(format.format(date));

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
    private View.OnClickListener SelectEDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerView pvTime = new TimePickerBuilder(BuKaDanListActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    et_EDate.setText(format.format(date));

                }
            }).setCancelText("取消")
                    .setSubmitText("确定")
                    .setType(new boolean[]{true,true,true,true,true,true})
                    .setLabel("年","月","日","时","分","秒")
                    .build();
            pvTime.show();
        }
    };
    //取消查询
    private View.OnClickListener CancelFind = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            layout_Filter.setVisibility(View.GONE);
        }
    };
    //提交查询
    private View.OnClickListener OnSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogUtil.ShowProgressDialog1("正在查询...",BuKaDanListActivity.this);
            String url = getResources().getString(R.string.url)+"FindBuKaDan";
            String sDaiShen = cb_DaiShen.isChecked()?"1":"0";
            String sJueJue = cb_JueJue.isChecked()?"1":"0";
            String sWanJie = cb_WanJie.isChecked()?"1":"0";
            String sDate = et_SDate.getText().toString();
            String eDate = et_EDate.getText().toString();
            RequestBody body = new FormBody.Builder()
                    .add("empno",empNo)
                    .add("daishen",sDaiShen)
                    .add("juejue",sJueJue)
                    .add("wanjie",sWanJie)
                    .add("sdate",sDate)
                    .add("edate",eDate)
                    .build();
            HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(BuKaDanListActivity.this,"服务器连接失败",Toast.LENGTH_LONG).show();
                            //dialogUtil.ShowAlertDialog("查询失败,请重试",BuKaDanListActivity.this);
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
                                buKaList.clear();
                                JSONArray jsonArray = new JSONArray(sResponse);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String s = jsonObject.toString();
                                Messages messages = new Gson().fromJson(s,Messages.class);
                                if(messages.status==1){
                                    JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                    if(jsonArray1!=null&&jsonArray1.length()>0){

                                        for(int i = 0;i<jsonArray1.length();i++){

                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                            String s1 = jsonObject1.toString();
                                            BuKaDanItem item = new Gson().fromJson(s1,BuKaDanItem.class);
                                            buKaList.add(item);
                                        }

                                    }
                                }
                                buKaDanAdapter.notifyDataSetChanged();
                            }catch (Exception ex){
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(BuKaDanListActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                            }
                            finally {
                                dialogUtil.CloseProgressDialog1();
                            }
                        }
                    });
                }
            });

        }
    };
}
