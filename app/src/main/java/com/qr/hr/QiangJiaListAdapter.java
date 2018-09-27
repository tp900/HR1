package com.qr.hr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.QiangJiaDan;
import com.qr.hr.swipe.Menu;
import com.qr.hr.swipe.MenuItem;
import com.qr.hr.swipe.MySwipeLayout;
import com.qr.hr.swipe.OnMenuClickListener;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.ProcessDialogUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QiangJiaListAdapter extends BaseAdapter {
    List<QiangJiaDan> list;
    Context context;
    private int mWidth;
    private int downX= 0;
    private int moveX= 0;
    private String TAG="TAG";
    private boolean isOpen = false;//菜单开|状态
    private int openPostion=-1;//当前打开的项
    private ProcessDialogUtil dialogUtil = new ProcessDialogUtil();
    private AlertDialog alertDialog;
    public QiangJiaListAdapter(List<QiangJiaDan> list,Context context){
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
        QiangJiaDan qiangJiaDan = list.get(position);

        if(qiangJiaDan.status.equals("待审核")){
            return 0;
        }else{return 1;}

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        MySwipeLayout mySwipeLayout = null;
        if(convertView==null){
            convertView = View.inflate(context,R.layout.qingjilist_item,null);
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
        QiangJiaDan leave = list.get(position);
        String sDays = leave.days+"";
        String sHours = leave.hours+"";
        sDays = sDays.endsWith(".0")?sDays.replace(".0",""):sDays;
        sHours = sHours.endsWith(".0")?sHours.replace(".0",""):sHours;
        holder.qj_days.setText("时长: "+sDays +" 天 "+sHours +" 小时");
        holder.qj_status.setText("状态: "+leave.status);
        holder.qj_date.setText("时间: "+leave.sDate+" 至 "+ leave.eDate);
        switch (leave.status){
            case "待审核":
                holder.qj_head.setBackground(new ColorDrawable(Color.rgb(229, 224,64)));
                break;
            case "已批准":
                holder.qj_head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                break;
            case "未批准":
                holder.qj_head.setBackground(new ColorDrawable(Color.rgb(249, 64,39)));
                break;
            default:
                holder.qj_status.setText("状态: 已批准");
                holder.qj_head.setBackground(new ColorDrawable(Color.rgb(46, 177,245)));
                break;
        }
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
                    if(isOpen){
                        isOpen = false;
                        openPostion = -1;
                    }
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
        TextView qj_head;
        TextView qj_days;
        TextView qj_status;
        TextView qj_date;
        public ViewHolder(View view) {
            qj_head = view.findViewById(R.id.qj_head);
            qj_days = view.findViewById(R.id.qj_days);
            qj_status = view.findViewById(R.id.qj_status);
            qj_date = view.findViewById(R.id.qj_date);
            view.setTag(this);
        }
    }
    public void CreateMenu(Menu menu){};
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                this.context.getResources().getDisplayMetrics());
    }
    private void MenuClick( final int postion){
        final QiangJiaDan leave = list.get(postion);
        //Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("提示!")
                .setMessage("确认要删除?")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //开始
                                alertDialog.dismiss();
                                dialogUtil.ShowProgressDialog1("处理中...",context);
                                //Utils.ShowProgressDialog("处理中...",QingJiaListActivity.this);
                                String url = "";
                                url =context.getResources().getString(R.string.url)+"CancleLeaveQJ";
                                RequestBody body1 = new FormBody.Builder()
                                        .add("id",leave.id+"").build();
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
                                        final String sResponse = response.body().string();
                                        ((Activity)context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    JSONObject jsonObject = new JSONObject(sResponse);
                                                    String s = jsonObject.toString();
                                                    Messages messages = new Gson().fromJson(s,Messages.class);
                                                    dialogUtil.CloseProgressDialog1();
                                                    Toast.makeText(context,messages.msg,Toast.LENGTH_LONG).show();
                                                    if(messages.status==1){
                                                        list.remove(postion);
                                                        notifyDataSetChanged();
                                                    }
                                                }catch (Exception ex){
                                                    dialogUtil.CloseProgressDialog1();
                                                    Toast.makeText(context,"未知错误,请重试",Toast.LENGTH_LONG).show();
                                                }finally {
                                                    dialogUtil.CloseProgressDialog1();
                                                }

                                            }
                                        });
                                    }
                                });
                            }

                            //结束
                        }
                )
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();

    }

}
