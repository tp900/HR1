package com.qr.hr;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.qr.hr.modles.Leave;

public class LeaveApprove extends Dialog {
    private Activity context;
    private Leave leave;
    public EditText editRemark;
    private Button submit;
    private View.OnClickListener onClickListener;
    public LeaveApprove(Activity context){
        super(context);
        this.context = context;
    }
    public LeaveApprove(Activity context, Leave leave, View.OnClickListener listener){
        super(context);
        this.context = context;
        this.leave = leave;
        this.onClickListener = listener;
    }
    public LeaveApprove(Activity context, Leave leave){
        super(context);
        this.context = context;
        this.leave = leave;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveapprove);
        editRemark = findViewById(R.id.leaveapproveremark);
        submit = findViewById(R.id.leaveapprovesubmit);
        Window dialogWindow = this.getWindow();
        WindowManager manager = context.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams pars = dialogWindow.getAttributes();
        pars.width = (int)(display.getWidth());
        dialogWindow.setAttributes(pars);
        submit.setOnClickListener(this.onClickListener);
        this.setCancelable(true);

    }
    public void OnClick(View.OnClickListener listener){
        if(null!=listener){
            this.onClickListener = listener;
            this.submit.setOnClickListener(this.onClickListener);
        }

    }
}
