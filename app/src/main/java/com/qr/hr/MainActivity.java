package com.qr.hr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.BuKaDanItem;
import com.qr.hr.modles.Leave;
import com.qr.hr.modles.Messages;
import com.qr.hr.utils.CustomerDialog;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;
import com.qr.hr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private Button bt_menu;
    private DrawerLayout drawerLayout;
    private Button bt_exit;
    private List<Leave> leaves = new ArrayList<>();
    private SwipeMenuListView swipeMenuListView;
    private SwipeMenuCreator creator;
    private AppAdapter appAdapter;
    private String empno_;
    private Leave cLeave;
    private int cPostion;
    private LeaveApprove leaveApprove;
    private LeaveRefuse leaveRefuse;
    private LeaveTransfer leaveTransfer;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    private AlertDialog alertDialog;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.CloseProgressDialog();
        //Log.d("11", "onActivityResult: Main");
        InitDanBan();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_menu = findViewById(R.id.bt_menu);
        bt_exit = findViewById(R.id.bt_exit);
        drawerLayout = findViewById(R.id.drawerlayout);
        swipeMenuListView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InitDanBan();
            }
        });
        bt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //初始化待办清单
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        empno_ = editor.getString("emp_no","");
        InitDanBan();

        appAdapter = new AppAdapter(empno_);
        swipeMenuListView.setAdapter(appAdapter);
        //设置滑动菜单开始
         creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu0(menu);
                        break;
                    case 1:
                        createMenu1(menu);
                        break;
                    case 2:
                        createMenu2(menu);
                        break;
                }
            }

            private void createMenu0(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(R.color.colorTransfer);
                item1.setWidth(dp2px(90));
                item1.setTitle("转交");
                item1.setTitleColor(Color.WHITE);
                item1.setTitleSize(18);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(R.color.colorPass);
                item2.setWidth(dp2px(90));
                item2.setTitle("同意");
                item2.setTitleColor(Color.WHITE);
                item2.setTitleSize(18);
                menu.addMenuItem(item2);
                SwipeMenuItem item3 = new SwipeMenuItem(
                        getApplicationContext());
                item3.setBackground(R.color.colorRefuse);
                item3.setWidth(dp2px(90));
                item3.setTitle("拒绝");
                item3.setTitleColor(Color.WHITE);
                item3.setTitleSize(18);
                menu.addMenuItem(item3);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(R.color.colorPass);
                item2.setWidth(dp2px(90));
                item2.setTitle("同意");
                item2.setTitleColor(Color.WHITE);
                item2.setTitleSize(18);
                menu.addMenuItem(item2);
                SwipeMenuItem item3 = new SwipeMenuItem(
                        getApplicationContext());
                item3.setBackground(R.color.colorRefuse);
                item3.setWidth(dp2px(90));
                item3.setTitle("拒绝");
                item3.setTitleColor(Color.WHITE);
                item3.setTitleSize(18);
                menu.addMenuItem(item3);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
                        0xF5)));
                item1.setWidth(dp2px(90));
                item1.setIcon(R.drawable.ic_action_about);

                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.ic_action_share);
                menu.addMenuItem(item2);
            }
        };
         swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_LONG).show();
                 Leave leave = leaves.get(position);
                 if(leave.leaveType.equals("补卡")){
                     dialogUtil.ShowProgressDialog1("加载中...",MainActivity.this);
                     String url = getResources().getString(R.string.url)+"GetBuKaDan";
                     RequestBody body1 = new FormBody.Builder().add("id",leave.id+"").build();
                     HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                         @Override
                         public void onFailure(Call call, IOException e) {
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     dialogUtil.CloseProgressDialog1();
                                     Toast.makeText(MainActivity.this,"服务器连接失败,请重试",Toast.LENGTH_SHORT).show();
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
                                        JSONArray jsonArray = new JSONArray(sResponse);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String s = jsonObject.toString();
                                        Messages messages = new Gson().fromJson(s,Messages.class);
                                        if(messages.status==1){
                                            JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                            String s1 = jsonObject1.toString();
                                            BuKaDanItem item = new Gson().fromJson(s1,BuKaDanItem.class);
                                            dialogUtil.CloseProgressDialog1();
                                            alertDialog = CustomerDialog.ShowDialog(MainActivity.this, item, new DialogCallBack() {
                                                @Override
                                                public void IsSure(Object obj) {
                                                    alertDialog.dismiss();
                                                }

                                                @Override
                                                public void IsCancel() {

                                                }
                                            });
                                        }else{
                                            dialogUtil.CloseProgressDialog1();
                                            Toast.makeText(MainActivity.this,messages.msg,Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
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
        //设置滑动菜单结束
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                final Leave leave = leaves.get(position);
                cLeave = leave;
                cPostion = position;
                if(leave.leaveType.equals("补卡")){
                    //补卡单
                    switch (index){
                        case 0:
                            //Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                            BuKaDanProcess(leave,"1",position);
                            break;
                        case 1:
                            BuKaDanProcess(leave,"2",position);
                            break;
                    }
                }else{
                    //请假与调休
                    switch (index){
                        case 0:
                            //Toast.makeText(MainActivity.this,"转交",Toast.LENGTH_SHORT).show();
                            leaveTransfer = new LeaveTransfer(MainActivity.this,leave,listener2);
                            leaveTransfer.show();
                            break;
                        case 1:
                            //Toast.makeText(MainActivity.this,"同意",Toast.LENGTH_SHORT).show();
                            leaveApprove = new LeaveApprove(MainActivity.this, leave,listener);
                            leaveApprove.show();
                            break;
                        case 2:
                            //Toast.makeText(MainActivity.this,"拒绝",Toast.LENGTH_SHORT).show();
                            leaveRefuse = new LeaveRefuse(MainActivity.this,leave,listener1);
                            leaveRefuse.show();
                            break;
                    }
                }
                return true;
            }
        });

    }
    //oncreate结束
    private void InitDanBan(){
        final RequestBody body = new FormBody.Builder()
                .add("empno",empno_).build();
        //String url = "http://192.168.22.17/Services/API.asmx/DaiBan";
        dialogUtil.ShowProgressDialog1("加载中...",MainActivity.this);
        String url = getResources().getString(R.string.url)+"DaiBan";
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.CloseProgressDialog1();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,"服务器连接失败",Toast.LENGTH_LONG).show();
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
                            leaves.clear();
                            JSONArray jsonArray = new JSONArray(sResponse);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String conetxt = jsonObject.toString();
                            Messages messages = new Gson().fromJson(conetxt,Messages.class);
                            if(messages.status==1){
                                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                                for(int i = 0 ;i< jsonArray1.length();i++){
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    String s = jsonObject1.toString();
                                    Leave leave = new Gson().fromJson(s,Leave.class);
                                    leaves.add(leave);
                                }

                            }
                            appAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }
                });
            }
        });
    }

    //处理补卡单
    private void BuKaDanProcess(Leave leave, String status, final int postion) {
        dialogUtil.ShowProgressDialog1("处理中...",MainActivity.this);
        String url = getResources().getString(R.string.url)+"ConfrimBuKa";
        RequestBody body = new FormBody.Builder()
                .add("id",leave.id+"")
                .add("empno",leave.approveEmpNo)
                .add("status",status)
                .build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.CloseProgressDialog1();
                        Toast.makeText(MainActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
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
                                leaves.remove(postion);
                                appAdapter.notifyDataSetChanged();
                            }
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(MainActivity.this,messages.msg,Toast.LENGTH_LONG).show();
                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                        }
                    }
                });
            }
        });

    }
    //onCreated方法结束
    private  View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.leaveapprovesubmit:
                    //leaveApprove.cancel();
                    dialogUtil.ShowProgressDialog1("处理中...",MainActivity.this);
                    //Utils.ShowProgressDialog("处理中...",MainActivity.this);
                    //Toast.makeText(MainActivity.this,"同意",Toast.LENGTH_SHORT).show();
                    //EditText remark = leaveApprove.editRemark;
                    String sRemark = leaveApprove.editRemark.getText().toString();
                    RequestBody body1 = new FormBody.Builder()
                            .add("leaveid",cLeave.id+"")
                            .add("leavetype",cLeave.leaveType)
                            .add("approve",cLeave.approveEmpNo)
                            .add("remark",sRemark).build();
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveApprove";
                    String url = getResources().getString(R.string.url)+"LeaveApprove";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(MainActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
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
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            leaves.remove(cPostion);
                                            appAdapter.notifyDataSetChanged();
                                            leaveApprove.cancel();
                                        }
                                        Toast.makeText(MainActivity.this,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                                    }
                                    finally {
                                        dialogUtil.CloseProgressDialog1();
                                    }
                                }
                            });
                        }
                    });

                    break;

            }
        }
    };
    //拒绝监听器
    private  View.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.leaverefusesubmit:
                    //Toast.makeText(MainActivity.this,"同意",Toast.LENGTH_SHORT).show();
                    //EditText remark = leaveApprove.editRemark;
                    //Utils.ShowProgressDialog("处理中...",MainActivity.this);
                    dialogUtil.ShowProgressDialog1("处理中...",MainActivity.this);
                    String sRemark = leaveRefuse.editRemark.getText().toString();
                    RequestBody body1 = new FormBody.Builder()
                            .add("leaveid",cLeave.id+"")
                            .add("leavetype",cLeave.leaveType)
                            .add("approve",cLeave.approveEmpNo)
                            .add("remark",sRemark).build();
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveRefuse";
                    String url = getResources().getString(R.string.url)+"LeaveRefuse";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(MainActivity.this,"服务器连接失败",Toast.LENGTH_LONG).show();
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
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            leaves.remove(cPostion);
                                            appAdapter.notifyDataSetChanged();
                                            leaveRefuse.cancel();
                                        }
                                        Toast.makeText(MainActivity.this,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                                    }finally {
                                        dialogUtil.CloseProgressDialog1();
                                    }
                                }
                            });
                        }
                    });

                    break;

            }
        }
    };
    //转交监听器
    private  View.OnClickListener listener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.leavetransfersubmit:
                    //Utils.ShowProgressDialog("处理中...",MainActivity.this);
                    //Toast.makeText(MainActivity.this,"同意",Toast.LENGTH_SHORT).show();
                    //EditText remark = leaveApprove.editRemark;
                    dialogUtil.ShowProgressDialog1("处理中...",MainActivity.this);
                    String sRemark = leaveTransfer.editRemark.getText().toString();
                    String sNextApprove = leaveTransfer.editNextApprove.getText().toString();
                    if(sNextApprove.isEmpty()){
                        dialogUtil.CloseProgressDialog1();
                        Toast.makeText(MainActivity.this,"请输入下一位处理人",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RequestBody body1 = new FormBody.Builder()
                            .add("leaveid",cLeave.id+"")
                            .add("leavetype",cLeave.leaveType)
                            .add("approve",cLeave.approveEmpNo)
                            .add("nextapprove",sNextApprove)
                            .add("remark",sRemark).build();
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveTransfer";
                    String url = getResources().getString(R.string.url)+"LeaveTransfer";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(MainActivity.this,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
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
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            leaves.remove(cPostion);
                                            appAdapter.notifyDataSetChanged();
                                            leaveTransfer.cancel();
                                        }
                                        Toast.makeText(MainActivity.this,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_LONG).show();
                                    }finally {
                                        dialogUtil.CloseProgressDialog1();
                                    }
                                }
                            });
                        }
                    });

                    break;

            }
        }
    };
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
            Leave leave = leaves.get(position);
            if(leave.leaveType.equals("补卡")){
                return 1;
            }else{
                return 0;
            }
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.daibanitem, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Leave leave = leaves.get(position);
            holder.item_head.setText(leave.leaveType);
            holder.item_empname.setText("姓名:"+leave.empName);
            holder.item_dept.setText("部门:"+leave.dept);
            holder.item_date.setText("时间:"+leave.sDate+" 至 "+ leave.eDate);
            holder.item_remark.setText("事由:"+leave.remark);
            return convertView;
        }

        class ViewHolder {
            TextView item_head;
            TextView item_empname;
            TextView item_dept;
            TextView item_date;
            TextView item_remark;
            public ViewHolder(View view) {
                item_head = view.findViewById(R.id.item_head);
                item_empname = view.findViewById(R.id.item_empname);
                item_dept = view.findViewById(R.id.item_dept);
                item_date = view.findViewById(R.id.item_date);
                item_remark = view.findViewById(R.id.item_remark);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
