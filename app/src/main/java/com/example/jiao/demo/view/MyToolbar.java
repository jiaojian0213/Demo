package com.example.jiao.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jiaojian on 2017/9/18.
 */

public class MyToolbar extends Toolbar {

    private TextView toolBarTitle;

    public MyToolbar(Context context) {
        super(context);
        initView();
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView(){
        toolBarTitle = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        toolBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        toolBarTitle.setTextColor(Color.WHITE);
//        toolBarTitle.getPaint().setFakeBoldText(true);//加粗
        addView(toolBarTitle,params);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        toolBarTitle.setText(title);
    }

//    @Override
//    public void setNavigationOnClickListener(OnClickListener listener) {
//        super.setNavigationOnClickListener(DebounceUtils.getProxy(listener));
//    }
//
//    @Override
//    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
//        super.setOnMenuItemClickListener(DebounceUtils.getProxy(listener));
//    }
}
