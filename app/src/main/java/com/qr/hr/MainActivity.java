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
import com.qr.hr.swipe.Menu;
import com.qr.hr.swipe.MenuCreator;
import com.qr.hr.swipe.MenuItem;
import com.qr.hr.swipe.MyListView;
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
    private MyListView swipeMenuListView;
    private SwipeMenuCreator creator;
    private MainAdapter appAdapter;
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
        CheckVer();
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //初始化待办清单
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        empno_ = editor.getString("emp_no", "");
        InitDanBan();
        InitAdapter();
        swipeMenuListView.setAdapter(appAdapter);
        CheckVer();
    }
    //初始化Adapter
    private void InitAdapter(){
        final MenuCreator menuCreator = new MenuCreator() {
            @Override
            public void CreateMenu(Menu menu) {
               switch (menu.getMenuType()){
                   case 0:
                       createMenu(menu);
                       break;
                   case 1:
                       createMenu1(menu);
                       break;
               }
            }
            private void createMenu(Menu menu) {
                MenuItem item1 = new MenuItem(getApplicationContext());
                item1.setBackground(R.color.colorTransfer);
                item1.setWidth(dp2px(50));
                item1.setTitle("转交");
                item1.setTitleColor(Color.WHITE);
                item1.setTitleSize(18);
                item1.setId(0);
                menu.menuItems.add(item1);
                MenuItem item2 = new MenuItem(getApplicationContext());
                item2.setBackground(R.color.colorPass);
                item2.setWidth(dp2px(50));
                item2.setTitle("同意");
                item2.setTitleColor(Color.WHITE);
                item2.setTitleSize(18);
                item2.setId(1);
                menu.menuItems.add(item2);
                MenuItem item3 = new MenuItem(getApplicationContext());
                item3.setBackground(R.color.colorRefuse);
                item3.setWidth(dp2px(50));
                item3.setTitle("拒绝");
                item3.setTitleColor(Color.WHITE);
                item3.setTitleSize(18);
                item3.setId(2);
                menu.menuItems.add(item3);
            }

            private void createMenu1(Menu menu) {
                MenuItem item2 = new MenuItem(getApplicationContext());
                item2.setBackground(R.color.colorPass);
                item2.setWidth(dp2px(50));
                item2.setTitle("同意");
                item2.setTitleColor(Color.WHITE);
                item2.setTitleSize(18);
                item2.setId(3);
                menu.menuItems.add(item2);
                MenuItem item3 = new MenuItem(getApplicationContext());
                item3.setBackground(R.color.colorRefuse);
                item3.setWidth(dp2px(50));
                item3.setTitle("拒绝");
                item3.setTitleColor(Color.WHITE);
                item3.setTitleSize(18);
                item3.setId(4);
                menu.menuItems.add(item3);
            }
        };
        appAdapter = new MainAdapter(leaves,MainActivity.this){
            @Override
            public void CreateMenu(Menu menu) {
                menuCreator.CreateMenu(menu);
            }
        };
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


    //onCreated方法结束



    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void CheckVer(){

        String cVer = getResources().getString(R.string.ver);
        String url = getResources().getString(R.string.url)+"CheckVer";
        FormBody body = new FormBody.Builder().add("ver",cVer).build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String sResponse = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(sResponse);
                    String content = jsonObject.toString();
                    Messages messages = new Gson().fromJson(content,Messages.class);
                    if(messages.status==1){
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Intent intent = new Intent(MainActivity.this,UpdateActivity.class);
                                  startActivity(intent);
                                  MainActivity.this.finish();
                              }
                          });
                    }
                }catch (Exception ex){

                }
            }
        });

    }

}
