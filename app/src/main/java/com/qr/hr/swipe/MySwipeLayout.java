package com.qr.hr.swipe;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.List;

public class MySwipeLayout extends LinearLayout {
    private View contentView;
    private Scroller scroller;
    private List<MenuItem> menus;
    private int postion;
    private int mWidth;
    private OnMenuClickListener onMenuClickListener;
    public MySwipeLayout(Context context) {
        super(context);
    }

    public MySwipeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwipeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public MySwipeLayout(View contentView,List<MenuItem> menus){
        super(contentView.getContext());
        this.contentView = contentView;
        this.menus = menus;
        Init();
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }


    public OnMenuClickListener getOnMenuClickListener() {
        return onMenuClickListener;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),0);
            invalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
    private void Init(){
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);
        contentView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        addView(contentView);
        if(menus!=null&&menus.size()>0){
            for (final MenuItem menu:menus) {
                final LinearLayout linearLayout = new LinearLayout(this.getContext());
                LayoutParams params = new LayoutParams(menu.getWidth(),LayoutParams.MATCH_PARENT);
                linearLayout.setLayoutParams(params);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setBackground(menu.getBackground());
                TextView textView = new TextView(linearLayout.getContext());
                textView.setText(menu.getTitle());
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(menu.getTitleColor());
                linearLayout.addView(textView);
                linearLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(null!=onMenuClickListener){
                            onMenuClickListener.OnClick(menu.getId(),getPostion());
                        }
                    }
                });
                addView(linearLayout);
            }
        }
        scroller = new Scroller(this.getContext());
    }
    public void Swipe(int x){
        this.scroller.startScroll(0,0,x,0,300);
        invalidate();
    }
}
