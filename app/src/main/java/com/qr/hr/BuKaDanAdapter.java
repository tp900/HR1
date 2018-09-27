package com.qr.hr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qr.hr.interfaces.DialogCallBack;
import com.qr.hr.modles.BuKa;
import com.qr.hr.modles.BuKaDanItem;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.TiaoXiuDan;
import com.qr.hr.swipe.Menu;
import com.qr.hr.swipe.MenuItem;
import com.qr.hr.swipe.MySwipeLayout;
import com.qr.hr.swipe.OnMenuClickListener;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuKaDanAdapter extends BaseAdapter {
    private List<BuKaDanItem> buKaList;
    private Activity context;
    private String empNo;
    private int mWidth;
    private int downX= 0;
    private int moveX= 0;
    private String TAG="TAG";
    private boolean isOpen = false;//菜单开|状态
    private int openPostion=-1;//当前打开的项
    private AlertDialog alertDialog;
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    public BuKaDanAdapter(List<BuKaDanItem> list,String empNo,Activity context){
        this.buKaList=list;
        this.empNo = empNo;
        this.context = context;
    }
    @Override
    public int getCount() {
        return buKaList.size();
    }

    @Override
    public int getItemViewType(int position) {
        BuKaDanItem buKa = buKaList.get(position);
        if(buKa.status.equals("待审核")){
            return 0;//加载删除按钮
        }else{
            return 1;//不加载删除按钮
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MySwipeLayout mySwipeLayout = null;
        if(convertView==null){
            convertView = View.inflate(context,R.layout.bukadanlist_item,null);
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
        ViewHolder viewHolder = new ViewHolder(convertView);
        BuKaDanItem leave = buKaList.get(position);
        viewHolder.tv_BKDate.setText("日期:"+leave.bkdate);
        viewHolder.tv_Status.setText("状态:"+leave.status);
        viewHolder.tv_AM.setText("上午:"+leave.am);
        viewHolder.tv_PM.setText("下午:"+leave.pm);
        viewHolder.tv_JB.setText("加班:"+leave.jb);
        viewHolder.tv_YY.setText("原因:"+leave.reason);
        if(leave.am==null){
            viewHolder.tv_AM.setVisibility(View.GONE);
        }
        if(leave.pm==null){
            viewHolder.tv_PM.setVisibility(View.GONE);
        }
        if(leave.jb==null){
            viewHolder.tv_JB.setVisibility(View.GONE);
        }
        if(leave.reason==null){
            viewHolder.tv_YY.setText("原因:");
        }
        switch (leave.status){
            case "待审核":
                viewHolder.tv_Head.setBackground(new ColorDrawable(Color.rgb(229, 224,64)));
                break;
            case "已批准":
                viewHolder.tv_Head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                break;
            case "未批准":
                viewHolder.tv_Head.setBackground(new ColorDrawable(Color.rgb(249, 64,39)));
                break;
            default:
                viewHolder.tv_Status.setText("状态: 已批准");
                viewHolder.tv_Head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                break;
        }
        mySwipeLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void OnClick(int menuId, int menuType) {
                if(menuId==0){
                    MenuClick(position);
                    if(isOpen){
                        isOpen = false;
                        openPostion = -1;
                    }
                }

            }
        });
        mySwipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*list.remove(position);
                notifyDataSetChanged();*/

            }
        });
        mySwipeLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void OnClick(int menuId, int menuType) {
                if(menuId==0){
                    MenuClick(position);
                    //list.remove(position);
                    if(isOpen){
                        isOpen = false;
                        openPostion = -1;
                    }
                    //notifyDataSetChanged();
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
                        Log.i(TAG, "onTouch: Move");
                        moveX = (int)event.getX();
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, String.format("d:%1$s,m:%2$s",downX,moveX));
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return buKaList.get(position);
    }
    class ViewHolder{
        public TextView tv_Head;
        public TextView tv_BKDate;
        public TextView tv_Status;
        public TextView tv_AM;
        public TextView tv_PM;
        public TextView tv_JB;
        public TextView tv_YY;
        public ViewHolder(View view){
            tv_Head = view.findViewById(R.id.head);
            tv_BKDate = view.findViewById(R.id.bkdate);
            tv_Status = view.findViewById(R.id.status);
            tv_AM = view.findViewById(R.id.am);
            tv_PM = view.findViewById(R.id.pm);
            tv_JB = view.findViewById(R.id.jb);
            tv_YY = view.findViewById(R.id.yy);
        }
    }
    public void CreateMenu(Menu menu){};
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                this.context.getResources().getDisplayMetrics());
    }
    private void MenuClick(final int pos){
        dialogUtil.ShowAlertDialog("确定删除?", context, new DialogCallBack() {
            @Override
            public void IsSure(Object obj) {
                BuKaDanItem item = buKaList.get(pos);
                dialogUtil.ShowProgressDialog1("处理中...",context);
                String url =context.getResources().getString(R.string.url)+"CancelBuKa";
                RequestBody body = new FormBody.Builder().add("empno",item.approve).add("id",item.id+"").build();
                HttpUtil.SendOkHttpRequest(url, body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogUtil.CloseProgressDialog1();
                                Toast.makeText(context,"删除失败,请重试",Toast.LENGTH_LONG).show();
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
                                        buKaList.remove(pos);
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

            @Override
            public void IsCancel() {

            }
        });
    }
}
