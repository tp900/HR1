package com.qr.hr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.BuKaDanItem;
import com.qr.hr.modles.Leave;
import com.qr.hr.modles.Messages;
import com.qr.hr.swipe.Menu;
import com.qr.hr.swipe.MenuItem;
import com.qr.hr.swipe.MySwipeLayout;
import com.qr.hr.swipe.OnMenuClickListener;
import com.qr.hr.utils.CustomerDialog;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainAdapter extends BaseAdapter {
    List<Leave> list;
    Context context;
    private int mWidth;
    private int downX= 0;
    private int moveX= 0;
    private String TAG="TAG";
    private boolean isOpen = false;//菜单开|状态
    private int openPostion=-1;//当前打开的项
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    private AlertDialog alertDialog;
    private LeaveApprove leaveApprove;
    private LeaveRefuse leaveRefuse;
    private LeaveTransfer leaveTransfer;
    private Leave cLeave;
    private int cPostion;
    public MainAdapter(List<Leave> list,Context context){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Leave leave = list.get(position);
        switch (leave.leaveType){
            case "补卡":
                return 1;
           default:
                return 0;

        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        MySwipeLayout mySwipeLayout = null;
        if(convertView==null){
            convertView = View.inflate(context,R.layout.daibanitem,null);
            Menu menu = new Menu();
            menu.setMenuType(getItemViewType(position));
            CreateMenu(menu);
            int i = 0;
            for (MenuItem m:menu.menuItems) {
                i+=m.getWidth();
            }
            mySwipeLayout = new MySwipeLayout(convertView,menu.menuItems);
            mySwipeLayout.setPostion(position);
            mySwipeLayout.setmWidth(i);
        }else{
            mySwipeLayout = (MySwipeLayout)convertView;
            if(position==openPostion){
                mySwipeLayout.Swipe(0);
                isOpen=false;
                openPostion = -1;
            }else if (openPostion==-1){
                mySwipeLayout.Swipe(0);
                isOpen=false;
                openPostion = -1;
            }

        }
        ViewHolder holder = new ViewHolder(convertView);
        Leave leave = list.get(position);
        holder.item_head.setText(leave.leaveType);
        holder.item_empname.setText("姓名:"+leave.empName);
        holder.item_dept.setText("部门:"+leave.dept);
        holder.item_date.setText("时间:"+leave.sDate+" 至 "+ leave.eDate);
        holder.item_remark.setText("事由:"+leave.remark);
        mySwipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Leave leave1 = list.get(position);
                cLeave = leave1;
                cPostion = position;
                FindBuKaDan(leave1);

            }
        });
        mySwipeLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void OnClick(int menuId, int menuType) {
                Leave leave1 = list.get(position);
                cLeave = leave1;
                cPostion = position;
                switch (menuId){
                    case 0:
                        leaveTransfer = new LeaveTransfer((Activity) context,leave1,listener2);
                        leaveTransfer.show();
                        break;
                    case 1:
                        leaveApprove = new LeaveApprove((Activity) context, leave1,listener);
                        leaveApprove.show();
                        break;
                    case 2:
                        leaveRefuse = new LeaveRefuse((Activity) context,leave1,listener1);
                        leaveRefuse.show();
                        break;
                    case 3:
                        //补卡同意
                        BuKaDanProcess(leave1,"1",position);
                        break;
                    case 4:
                        //补卡拒绝
                        BuKaDanProcess(leave1,"2",position);
                        break;
                }
                if(menuId==0){
                    list.remove(position);
                    if(isOpen){
                        isOpen = false;
                        openPostion = -1;
                    }
                    notifyDataSetChanged();
                }

            }
        });
        mySwipeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX = (int)event.getX();
                        moveX = (int)event.getX();
                    case MotionEvent.ACTION_MOVE:
                        moveX = (int)event.getX();
                    case MotionEvent.ACTION_UP:
                        if(moveX-downX>20){
                            //右滑
                            if(isOpen&&openPostion==position){
                                MySwipeLayout swipeLayout = (MySwipeLayout)v;
                                ((MySwipeLayout) v).Swipe(0);
                                isOpen = false;
                                openPostion = -1;
                            }

                            return true;
                        }
                        if(moveX-downX<-20){
                            //左滑
                            if(!isOpen){
                                //Log.i(TAG, "onTouch: "+position);
                                ((MySwipeLayout) v).Swipe(((MySwipeLayout) v).getmWidth());
                                if(((MySwipeLayout) v).getmWidth()>0){
                                    isOpen = true;
                                    openPostion = position;
                                }

                            }

                            return true;
                        }

                    default:
                        return false;
                    //break;
                }
            }
        });
        return mySwipeLayout;
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
    public void CreateMenu(Menu menu){};
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                this.context.getResources().getDisplayMetrics());
    }
    //处理补卡单
    private void BuKaDanProcess(Leave leave, String status, final int postion) {
        dialogUtil.ShowProgressDialog1("处理中...",context);
        String url =context.getResources().getString(R.string.url)+"ConfrimBuKa";
        RequestBody body = new FormBody.Builder()
                .add("id",leave.id+"")
                .add("empno",leave.approveEmpNo)
                .add("status",status)
                .build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.CloseProgressDialog1();
                        Toast.makeText(context,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String sResponse = response.body().string();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(sResponse);
                            String s = jsonObject.toString();
                            Messages messages = new Gson().fromJson(s,Messages.class);
                            if(messages.status==1){
                                list.remove(postion);
                                notifyDataSetChanged();
                            }
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(context,messages.msg,Toast.LENGTH_LONG).show();
                        }catch (Exception ex){
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(context,"未知错误",Toast.LENGTH_LONG).show();
                        }finally {
                            dialogUtil.CloseProgressDialog1();
                        }
                    }
                });
            }
        });
    }
    //查询补卡单明细
    private void FindBuKaDan(Leave leave){
        if(leave.leaveType.equals("补卡")){
            dialogUtil.ShowProgressDialog1("加载中...",context);
            String url =context.getResources().getString(R.string.url)+"GetBuKaDan";
            RequestBody body1 = new FormBody.Builder().add("id",leave.id+"").build();
            HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.CloseProgressDialog1();
                            Toast.makeText(context,"服务器连接失败,请重试",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String sResponse = response.body().string();
                    ((Activity)context).runOnUiThread(new Runnable() {
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
                                    alertDialog = CustomerDialog.ShowDialog(((Activity)context), item, new DialogCallBack() {
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
                                    Toast.makeText(context,messages.msg,Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception ex){
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(context,"未知错误",Toast.LENGTH_SHORT).show();
                            }finally {
                                dialogUtil.CloseProgressDialog1();
                            }

                        }
                    });
                }
            });
        }
    }
    private  View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.leaveapprovesubmit:
                    //leaveApprove.cancel();
                    dialogUtil.ShowProgressDialog1("处理中...",context);
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
                    String url =context.getResources().getString(R.string.url)+"LeaveApprove";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(context,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final  String sResponse = response.body().string();
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        //Utils.CloseProgressDialog();
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            list.remove(cPostion);
                                            notifyDataSetChanged();
                                            leaveApprove.cancel();
                                        }
                                        Toast.makeText(context,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(context,"未知错误",Toast.LENGTH_LONG).show();
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
                    dialogUtil.ShowProgressDialog1("处理中...",context);
                    String sRemark = leaveRefuse.editRemark.getText().toString();
                    RequestBody body1 = new FormBody.Builder()
                            .add("leaveid",cLeave.id+"")
                            .add("leavetype",cLeave.leaveType)
                            .add("approve",cLeave.approveEmpNo)
                            .add("remark",sRemark).build();
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveRefuse";
                    String url =context.getResources().getString(R.string.url)+"LeaveRefuse";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(context,"服务器连接失败",Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final  String sResponse = response.body().string();
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        //Utils.CloseProgressDialog();
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            list.remove(cPostion);
                                            notifyDataSetChanged();
                                            leaveRefuse.cancel();
                                        }
                                        Toast.makeText(context,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(context,"未知错误",Toast.LENGTH_LONG).show();
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
                    dialogUtil.ShowProgressDialog1("处理中...",context);
                    String sRemark = leaveTransfer.editRemark.getText().toString();
                    String sNextApprove = leaveTransfer.editNextApprove.getText().toString();
                    if(sNextApprove.isEmpty()){
                        dialogUtil.CloseProgressDialog1();
                        Toast.makeText(context,"请输入下一位处理人",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RequestBody body1 = new FormBody.Builder()
                            .add("leaveid",cLeave.id+"")
                            .add("leavetype",cLeave.leaveType)
                            .add("approve",cLeave.approveEmpNo)
                            .add("nextapprove",sNextApprove)
                            .add("remark",sRemark).build();
                    //String url = "http://192.168.22.17/Services/API.asmx/LeaveTransfer";
                    String url =context.getResources().getString(R.string.url)+"LeaveTransfer";
                    HttpUtil.SendOkHttpRequest(url, body1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.CloseProgressDialog();
                                    dialogUtil.CloseProgressDialog1();
                                    Toast.makeText(context,"服务器连接失败,请重试",Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final  String sResponse = response.body().string();
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        //Utils.CloseProgressDialog();
                                        dialogUtil.CloseProgressDialog1();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String s = jsonObject.toString();
                                        Messages message = new Gson().fromJson(s,Messages.class);
                                        if(message.status==1){
                                            list.remove(cPostion);
                                            notifyDataSetChanged();
                                            leaveTransfer.cancel();
                                        }
                                        Toast.makeText(context,message.msg,Toast.LENGTH_LONG).show();
                                    }catch (Exception ex){
                                        dialogUtil.CloseProgressDialog1();
                                        Toast.makeText(context,"未知错误",Toast.LENGTH_LONG).show();
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

}
