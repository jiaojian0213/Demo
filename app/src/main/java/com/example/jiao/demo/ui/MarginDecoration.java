package com.example.jiao.demo.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.jiao.demo.utils.ConvertUtils;

public class MarginDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int margin;

    public MarginDecoration(Context context) {
        this.context = context;
        margin = ConvertUtils.dp2px(context, 10);

    }

//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//
////由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
//        outRect.set(10, 10, 10, 10);
////        if (parent.getChildLayoutPosition(view) % 3 == 0) {
////            outRect.set(margin, 0, margin, 0);
////
////        } else {
////
////            outRect.set(0, 0, margin, 0);
////        }
//    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = convertDpToPixel(2);
//        outRect.bottom = 0;
//        // top bottom left right 对应的值应该是dpi 而不是dp  dpi根据不同手机而不同
//        outRect.left = convertDpToPixel(7);
//        outRect.right = convertDpToPixel(7);
//        int i = parent.getChildLayoutPosition(view) % 3;//每行3个
//        switch (i) {
//            case 0://第一个
//                outRect.left = convertDpToPixel(0);
//                outRect.right = convertDpToPixel(6);
//                break;
//            case 1://第二个
//                outRect.left = convertDpToPixel(0);
//                outRect.right = convertDpToPixel(0);
//                break;
//            case 2://第三个
//                outRect.left = convertDpToPixel(6);
//                outRect.right = convertDpToPixel(0);
//                break;
//        }
    }


    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

}