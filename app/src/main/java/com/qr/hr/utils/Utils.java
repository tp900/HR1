package com.qr.hr.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.TypedValue;

public class Utils {
    private static ProgressDialog progressDialog;

    public static void ShowProgressDialog(String msg, Context context){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(msg);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public static void CloseProgressDialog(){
        if(null!=progressDialog){
            progressDialog.dismiss();
        }
        progressDialog= null;
    }
}
