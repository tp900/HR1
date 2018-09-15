package com.qr.hr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.qr.hr.modles.BuKa;
import com.qr.hr.modles.BuKaDanItem;
import com.qr.hr.modles.TiaoXiuDan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BuKaDanAdapter extends BaseAdapter {
    private List<BuKaDanItem> buKaList = new ArrayList<>();
    private Activity context;
    private String empNo;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = View.inflate(context,R.layout.bukadanlist_item,null);
            new ViewHolder(convertView);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
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
        return convertView;
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
            view.setTag(this);
        }
    }
}
