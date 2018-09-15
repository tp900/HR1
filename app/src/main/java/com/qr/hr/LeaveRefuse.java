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

public class LeaveRefuse extends Dialog {
    private Activity context;
    private Leave leave;
    public EditText editRemark;
    private Button submit;
    private View.OnClickListener onClickListener;
    public LeaveRefuse(Activity context){
        super(context);
        this.context = context;
    }
    public LeaveRefuse(Activity context, Leave leave, View.OnClickListener listener){
        super(context);
        this.context = context;
        this.leave = leave;
        this.onClickListener = listener;
    }
    public LeaveRefuse(Activity context, Leave leave){
        super(context);
        this.context = context;
        this.leave = leave;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaverefuse);
        editRemark = findViewById(R.id.leaverefuseremark);
        submit = findViewById(R.id.leaverefusesubmit);
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
