package com.qr.hr.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.qr.hr.R;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.BuKaDanItem;

public class CustomerDialog {
    public static AlertDialog ShowDialog(Activity context, BuKaDanItem item, final DialogCallBack callBack){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        final Window window = alertDialog.getWindow();
        window.setContentView(R.layout.bukadan_detail);
        window.getDecorView().setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));
        window.setLayout(window.getContext().getResources().getDisplayMetrics().widthPixels,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
         TextView tv_BKDate=window.findViewById(R.id.bkdate);
         TextView tv_Status= window.findViewById(R.id.status);
         TextView tv_AM= window.findViewById(R.id.am);
         TextView tv_PM = window.findViewById(R.id.pm);
         TextView tv_JB = window.findViewById(R.id.jb);
         TextView tv_YY = window.findViewById(R.id.yy);
        Button button = window.findViewById(R.id.close);
        tv_BKDate.setText("日期:"+item.bkdate);
        tv_Status.setText("状态:"+item.status);
        tv_AM.setText("上午:"+item.am);
        tv_PM.setText("下午:"+item.pm);
        tv_JB.setText("加班:"+item.jb);
        tv_YY.setText("原因:"+item.reason);
        if(item.am==null){
            tv_AM.setVisibility(View.GONE);
        }
        if(item.pm==null){
            tv_PM.setVisibility(View.GONE);
        }
        if(item.jb==null){
            tv_JB.setVisibility(View.GONE);
        }
        if(item.reason==null){
            tv_YY.setText("原因:");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=callBack){
                    callBack.IsSure(null);
                }
            }
        });
        return alertDialog;
    }
}
