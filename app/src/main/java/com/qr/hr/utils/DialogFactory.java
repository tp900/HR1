package com.qr.hr.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.qr.hr.QingJiaActivity;
import com.qr.hr.R;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.Approve;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogFactory {
    private static Approve cApprove;
    public static AlertDialog ShowSelectApprove(List<Approve> list,final Context context, final DialogCallBack callBack){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        final Window window = alertDialog.getWindow();
        window.setContentView(R.layout.selectapprove);
        window.getDecorView().setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));
        window.setLayout(window.getContext().getResources().getDisplayMetrics().widthPixels,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
        final Button bt_find = window.findViewById(R.id.bt_findfilter);
        final Button bt_close = window.findViewById(R.id.bt_closefilter);
        final RecyclerView recyclerView = window.findViewById(R.id.approvelist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        Myaddapter myaddapter = new Myaddapter(list, new DialogCallBack() {
            @Override
            public void IsSure(Object obj) {
                cApprove = (Approve)obj;
            }

            @Override
            public void IsCancel() {

            }
        });
        recyclerView.setAdapter(myaddapter);

        //查询按钮回调
        bt_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=callBack){
                    callBack.IsSure(cApprove);
                }
            }
        });
        //取消按钮回调
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=callBack){
                    callBack.IsCancel();
                }
            }
        });
        return alertDialog;
    }



   static class Myaddapter extends RecyclerView.Adapter<Myaddapter.ViewHolder>{
         private Context context;
         private List<Approve> list = new ArrayList<>();
         private DialogCallBack callBack;
         private int index;
         private boolean onBind;
         public Myaddapter(List<Approve> list,DialogCallBack callBack){
             this.list = list;
             this.callBack = callBack;
         }
         class ViewHolder extends RecyclerView.ViewHolder{
             RadioButton isChecked;
             TextView empNo;
             TextView empName;
             TextView postion;
            public ViewHolder(View itemView) {
                super(itemView);
                isChecked = itemView.findViewById(R.id.approve_checked);
                empName = itemView.findViewById(R.id.approve_empname);
                empNo = itemView.findViewById(R.id.approve_empno);
                postion = itemView.findViewById(R.id.approve_postion);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(context == null){
                context= parent.getContext();
            }
            View view = LayoutInflater.from(context).inflate(R.layout.selectapprove_item,parent,false);
            final ViewHolder viewHolder = new ViewHolder(view);

             return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
             onBind = true;
             final Approve approve = list.get(position);
             holder.empNo.setText("工号:"+approve.empNo);
             holder.empName.setText("姓名:"+approve.empName);
             holder.postion.setText("职位:"+approve.postion);
             holder.isChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        index = position;
                        if(callBack!=null){
                            callBack.IsSure(approve);
                        }
                        if(!onBind){
                            notifyDataSetChanged();
                        }

                    }
                }
            });
            if(index==position){
                holder.isChecked.setChecked(true);
            }else{
                holder.isChecked.setChecked(false);
            }
            onBind = false;
        }
    }

}
