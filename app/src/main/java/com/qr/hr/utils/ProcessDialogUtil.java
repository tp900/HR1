package com.qr.hr.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.qr.hr.interfaces.DialogCallBack;

public class ProcessDialogUtil {
    private ProgressDialog progressDialog1;
    private AlertDialog alertDialog;
    public void ShowProgressDialog1(String msg,Context context){
        if(progressDialog1==null){
            progressDialog1 = new ProgressDialog(context);
            progressDialog1.setMessage(msg);
            progressDialog1.setCanceledOnTouchOutside(false);
        }
        progressDialog1.show();
    }
    public void CloseProgressDialog1(){
        if(null!=progressDialog1){
            progressDialog1.dismiss();
        }
        progressDialog1= null;
    }
    public void ShowAlertDialog(String msg,Context context){
        if(alertDialog==null){
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }
    public void ShowAlertDialog(String msg, Context context, final DialogCallBack callBack){
        if(alertDialog==null){
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            if(callBack!=null){
                                callBack.IsSure(null);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }
}
