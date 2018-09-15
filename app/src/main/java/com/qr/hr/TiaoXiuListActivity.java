package com.qr.hr;

import android.app.AlertDialog;
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
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.QiangJiaDan;
import com.qr.hr.modles.TiaoXiuDan;
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

public class TiaoXiuListActivity extends BaseActivity {
    private List<TiaoXiuDan> leaves =new ArrayList<>();
    private SwipeMenuListView swipeMenuListView;
    private Button bt_back;
    private Button bt_add;
    private TiaoXiuDan cLeave;
    private int cPostion;
    private String empNo;
    private TiaoXiuListActivity.AppAdapter appAdapter;
    private SwipeMenuCreator creator;
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
        setContentView(R.layout.activity_tiao_xiu_list);
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
        InitTiaoXiuList(empNo);
        appAdapter = new TiaoXiuListActivity.AppAdapter(empNo);
        swipeMenuListView.setAdapter(appAdapter);
        InitCreator();
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnMenuItemClickListener(swipeMenuonClick);
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
                InitTiaoXiuList(empNo);
            }
        });
    }
    //设置滑动菜单
    private void InitCreator(){
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                if(menu.getViewType()==2){
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
    //滑动菜单点击事件
    private SwipeMenuListView.OnMenuItemClickListener swipeMenuonClick = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

            final TiaoXiuDan leave = leaves.get(position);
            cLeave = leave;
            cPostion = position;
            //Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
            alertDialog = new AlertDialog.Builder(TiaoXiuListActivity.this)
                    .setTitle("提示!")
                    .setMessage("确认要删除?")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //开始
                                    alertDialog.dismiss();
                                    dialogUtil.ShowProgressDialog1("处理中...",TiaoXiuListActivity.this);
                                    //Utils.ShowProgressDialog("处理中...",TiaoXiuListActivity.this);
                                    String url = "";
                                    url = getResources().getString(R.string.url)+"CancleLeaveTX";
                                    RequestBody body1 = new FormBody.Builder()
                                            .add("id",leave.id+"").build();
                                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Utils.CloseProgressDialog();
                                                    dialogUtil.CloseProgressDialog1();
                                                    Toast.makeText(TiaoXiuListActivity.this, "服务器连接失败,请重试", Toast.LENGTH_LONG).show();
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
                                                        dialogUtil.CloseProgressDialog1();
                                                        Toast.makeText(TiaoXiuListActivity.this, messages.msg, Toast.LENGTH_LONG).show();
                                                        if(messages.status==1){
                                                            leaves.remove(cPostion);
                                                            appAdapter.notifyDataSetChanged();
                                                        }
                                                    }catch (Exception ex){
                                                        dialogUtil.CloseProgressDialog1();
                                                        Toast.makeText(TiaoXiuListActivity.this, "未知错误", Toast.LENGTH_LONG).show();
                                                    }finally {
                                                        dialogUtil.CloseProgressDialog1();
                                                    }


                                                }
                                            });
                                        }
                                    });
                                }

                                //结束
                            }
                    )
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .create();
            alertDialog.show();

            return false;
        }
    };
    //返回按钮
    private View.OnClickListener GoBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    //新增按钮
    private View.OnClickListener Add = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TiaoXiuListActivity.this,TiaoXiuActivity.class);
            startActivityForResult(intent,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //新增完成时返回重新加载数据
        if(resultCode==3){
            InitTiaoXiuList(empNo);
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
            dialogUtil.ShowProgressDialog1("查询中...",TiaoXiuListActivity.this);
            //Utils.ShowProgressDialog("查询中...",TiaoXiuListActivity.this);
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
            String url = getResources().getString(R.string.url)+"FindLeaveTX";
            HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Utils.CloseProgressDialog();
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(TiaoXiuListActivity.this,"服务器连接失败,请重试",Toast.LENGTH_SHORT);
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
                                            TiaoXiuDan dan = new Gson().fromJson(jsonStr1,TiaoXiuDan.class);
                                            leaves.add(dan);
                                        }

                                        qj_filter.setVisibility(View.GONE);

                                    }
                                }
                                dialogUtil.CloseProgressDialog1();
                                appAdapter.notifyDataSetChanged();
                            }catch (Exception ex){
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(TiaoXiuListActivity.this,"未知错误",Toast.LENGTH_SHORT);
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
            TimePickerView pvTime = new TimePickerBuilder(TiaoXiuListActivity.this, new OnTimeSelectListener() {
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
            TimePickerView pvTime = new TimePickerBuilder(TiaoXiuListActivity.this, new OnTimeSelectListener() {
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

    private void InitTiaoXiuList(String empNo){
        if(empNo.isEmpty()){
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        dialogUtil.ShowProgressDialog1("加载中...",TiaoXiuListActivity.this);
        RequestBody body = new FormBody.Builder()
                .add("empno",empNo).build();
        String url = getResources().getString(R.string.url)+"TiaoXiuList";
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.CloseProgressDialog1();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(TiaoXiuListActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
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
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
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
                                        TiaoXiuDan dan = new Gson().fromJson(jsonStr1,TiaoXiuDan.class);
                                        leaves.add(dan);
                                    }
                                }
                            }else{
                                Toast.makeText(TiaoXiuListActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                            }
                            appAdapter.notifyDataSetChanged();

                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(TiaoXiuListActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
    //内部类
    class AppAdapter extends BaseAdapter {
        private String empno;
        public AppAdapter(String empno){
            this.empno = empno;
        }
        @Override
        public int getCount() {
            return leaves.size();
        }

        @Override
        public Object getItem(int position) {

            return leaves.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            TiaoXiuDan leave = leaves.get(position);
            if(leave.status.trim().equals("待审核")){
                return 2;
            }else{
                return 0;
            }
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.qingjilist_item, null);
                new TiaoXiuListActivity.AppAdapter.ViewHolder(convertView);
            }
            TiaoXiuListActivity.AppAdapter.ViewHolder holder = (TiaoXiuListActivity.AppAdapter.ViewHolder) convertView.getTag();
            TiaoXiuDan leave = leaves.get(position);

            holder.qj_days.setText("原时间: "+leave.ySDate +" 至 "+leave.yEDate);
            holder.qj_status.setText("状态: "+leave.status);
            holder.qj_date.setText("休时间: "+leave.sDate+" 至 "+ leave.eDate);
            switch (leave.status){
                case "待审核":
                    holder.qj_head.setBackground(new ColorDrawable(Color.rgb(229, 224,64)));
                    break;
                case "已批准":
                    holder.qj_head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                    break;
                case "未批准":
                    holder.qj_head.setBackground(new ColorDrawable(Color.rgb(249, 64,39)));
                    break;
                default:
                    holder.qj_status.setText("状态: 已批准");
                    holder.qj_head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                    break;
            }
            return convertView;
        }

        class ViewHolder {
            TextView qj_head;
            TextView qj_days;
            TextView qj_status;
            TextView qj_date;
            public ViewHolder(View view) {
                qj_head = view.findViewById(R.id.qj_head);
                qj_days = view.findViewById(R.id.qj_days);
                qj_status = view.findViewById(R.id.qj_status);
                qj_date = view.findViewById(R.id.qj_date);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
