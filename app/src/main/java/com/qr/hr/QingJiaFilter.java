package com.qr.hr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.qr.hr.interfaces.DialogCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QingJiaFilter extends Dialog {
    private Activity context;
    private DialogCallBack dialogCallBack;
    private CheckBox daishen;
    private CheckBox juejue;
    private CheckBox wanjie;
    private EditText sEditDate;
    private EditText eEditDate;
    private Button bt_find;
    private Button bt_cancel;
    public QingJiaFilter(@NonNull Activity context) {
        super(context);
        this.context = context;
    }
    public QingJiaFilter(@NonNull Activity context, DialogCallBack callBack) {
        super(context);
        this.context =context;
        this.dialogCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qingjianfilter);
        Window dialogWindow = this.getWindow();
        dialogWindow.getDecorView().setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));
        dialogWindow.setLayout(dialogWindow.getContext().getResources().getDisplayMetrics().widthPixels,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        //WindowManager manager = context.getWindowManager();
        //Display display = manager.getDefaultDisplay();
        //WindowManager.LayoutParams pars = dialogWindow.getAttributes();
        //pars.width = (int)(display.getWidth());
        //dialogWindow.setAttributes(pars);
        juejue = findViewById(R.id.juejue);
        wanjie = findViewById(R.id.wanjie);
        daishen = findViewById(R.id.daishen);
        sEditDate = findViewById(R.id.qj_sdatefilter);
        eEditDate = findViewById(R.id.qj_edatefilter);
        bt_find = findViewById(R.id.bt_findfilter);
        bt_cancel = findViewById(R.id.bt_closefilter);
        sEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        sEditDate.setText(format.format(date));

                    }
                }).setCancelText("取消")
                        .setSubmitText("确定")
                        .setType(new boolean[]{true,true,true,true,true,true})
                        .setLabel("年","月","日","时","分","秒")
                        .build();
                pvTime.show();*/
                //呈现一个日期选择器:


            }
        });
        bt_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=dialogCallBack){
                    dialogCallBack.IsSure("");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=dialogCallBack){
                    dialogCallBack.IsCancel();
                }
            }
        });
    }
}
