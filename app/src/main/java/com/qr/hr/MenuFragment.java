package com.qr.hr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MenuFragment extends Fragment {
    private CircleImageView img_head;
    private TextView emp_no;
    private TextView emp_dept;
    private List<String> menus = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private int mIndex = 0;//菜单索引
    private boolean flg =false;//菜单是否点击
    private DrawerLayout drawerLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menufragment,container,false);
        img_head = view.findViewById(R.id.img_head);
        emp_no = view.findViewById(R.id.emp_no);
        emp_dept = view.findViewById(R.id.emp_dept);
        listView = view.findViewById(R.id.menu_list);
        menus.add("请假单");
        menus.add("调休单");
        menus.add("补卡单");
        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,menus);
        listView.setAdapter(arrayAdapter);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sEmpNO = preferences.getString("emp_no","");
        String sEmpName = preferences.getString("emp_name","");
        String sEmp_Sex = preferences.getString("emp_sex","");
        String sEmpDept = preferences.getString("emp_dept","");
        String sEmpPostion = preferences.getString("emp_postion","");
        String sEmpPhoto = preferences.getString("emp_photo","");
        emp_no.setText("工号:"+sEmpNO+" 姓名:"+sEmpName);
        emp_dept.setText("部门:"+sEmpDept+" 职位:"+sEmpDept);
        if(sEmpPhoto!=null && sEmpPhoto!=""){
            String url = sEmpPhoto;
            Glide.with(getContext()
                    .getApplicationContext())
                    .load(sEmpPhoto)
                    .listener(ErrorListener)
                    .placeholder(sEmp_Sex.equals("男")?R.drawable.boy:R.drawable.girl)
                    .error(R.drawable.error)
                    .dontAnimate()
                    .into(img_head);
        }else{
            if(sEmp_Sex.equals("男")){
                img_head.setImageResource(R.drawable.boy);
            }else{
                img_head.setImageResource(R.drawable.girl);
            }

        }
        Activity mainActivity = (MainActivity)getActivity();
        drawerLayout=  getActivity().findViewById(R.id.drawerlayout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Log.d("1", "onDrawerSlide: "+slideOffset+"");
                //slideOffset = 0 滑动关闭完成 slideOffset = 1 滑动展开完成
                if(slideOffset == 0){
                    if(flg){
                        switch (mIndex){
                            case 1:
                                Utils.ShowProgressDialog("加载中...",getContext());
                                Intent intent = new Intent(getContext(),QingJiaListActivity.class);
                                startActivityForResult(intent,1);
                                break;
                            case 2:
                                Utils.ShowProgressDialog("加载中...",getContext());
                                Intent intent1 = new Intent(getContext(),TiaoXiuListActivity.class);
                                startActivityForResult(intent1,1);
                                break;
                            case 3:
                                Utils.ShowProgressDialog("加载中...",getContext());
                                Intent intent2 = new Intent(getContext(),BuKaDanListActivity.class);
                                startActivityForResult(intent2,1);
                                break;
                        }
                    }
                    flg=false;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menu = menus.get(position);

                switch (menu){
                    case "请假单":
                        drawerLayout.closeDrawers();
                        mIndex = 1;
                        flg = true;

                        //getActivity().finish();
                        break;
                    case "调休单":

                        drawerLayout.closeDrawers();
                        mIndex = 2;
                        flg = true;
                        break;
                    case "补卡单":

                        drawerLayout.closeDrawers();
                        mIndex = 3;
                        flg = true;
                        break;
                }
            }
        });

    }
    private RequestListener<String,GlideDrawable> ErrorListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            //Log.e("1", "onException: "+e.getMessage() );
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    };

}
