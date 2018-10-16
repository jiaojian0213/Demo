package com.example.jiao.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.esri.core.symbol.PictureMarkerSymbol;

import java.math.BigDecimal;

public class MarkerUtils {

    /**
     * 创建带文字的 PictureMarkerSymbol
     *
     * @param text
     *            文字
     * @param textSize
     *            字体大小 14
     * @param textColor
     *            文字颜色 BLACK = Color.BLACK
     * @param rectColor
     *            边框颜色 BLACK = Color.BLACK
     * @param bgColor
     *            背景颜色 WHITE = Color.argb(100,255,255,255)
     * @return
     */
    public static PictureMarkerSymbol createTextPicMarkerSymbol(String text,
                                                                float textSize, int textColor, int rectColor, int bgColor,
                                                                boolean isAutoOffset, float angle, float xOffset, float yOffset) {
        // 创建带文本的图像
        Drawable drawable = createTextDrawable(text, textSize, textColor,
                rectColor, bgColor);
        PictureMarkerSymbol symbol = new PictureMarkerSymbol(drawable);

        if (isAutoOffset) {
            Rect rect = new Rect();
            Paint textPaint = new Paint();
            textPaint.setTextSize(textSize);
            textPaint.setColor(textColor);
            textPaint.getTextBounds(text, 0, text.length(), rect);
            int width = (rect.width() + 18) / 2 + 5;
            symbol.setOffsetX(width);
        } else {
            symbol.setAngle(angle);
            symbol.setOffsetX(xOffset);
            symbol.setOffsetY(yOffset);
        }
        return symbol;
    }


    /**
     * 创建带文字的 PictureMarkerSymbol
     *
     * @param text
     *            文字
     * @param textSize
     *            字体大小 14
     * @param textColor
     *            文字颜色 BLACK = Color.BLACK
     * @param rectColor
     *            边框颜色 BLACK = Color.BLACK
     * @param bgColor
     *            背景颜色 WHITE = Color.argb(100,255,255,255)
     * @return
     */
    public static PictureMarkerSymbol createTextPicMarkerSymbol(String text,
                                                                float textSize, int textColor, int rectColor, int bgColor,
                                                                boolean isAutoOffset) {
        PictureMarkerSymbol symbol = createTextPicMarkerSymbol(text, textSize,
                textColor, rectColor, bgColor, isAutoOffset, 0, 0, 0);
        return symbol;
    }

    /**
     * 创建带文字的 PictureMarkerSymbol
     *
     * @param text
     *            文字
     * @param textSize
     *            字体大小 14
     * @param textColor
     *            文字颜色 BLACK = Color.BLACK
     * @param rectColor
     *            边框颜色 BLACK = Color.BLACK
     * @param bgColor
     *            背景颜色 WHITE = Color.argb(100,255,255,255)
     * @return
     */
    public static PictureMarkerSymbol createTextPicMarkerSymbol(String text,
                                                                float textSize, int textColor, int rectColor, int bgColor) {
        PictureMarkerSymbol symbol = createTextPicMarkerSymbol(text, textSize,
                textColor, rectColor, bgColor, false, 0, 0, 0);
        return symbol;
    }

    /**
     * 创建文本图像
     *
     * @param text
     * @param textSize
     * @param textColor
     * @param rectColor
     * @param bgColor
     * @return
     */
    private static Drawable createTextDrawable(String text, float textSize,
                                               int textColor, int rectColor, int bgColor) {
        // 设置文本画笔
        Rect rect = new Rect();
        Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.DEFAULT); // 采用默认的宽度
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        int width = rect.width() + 18;
        int height = rect.height() + 16;

        // 创建图像
        Bitmap imgTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        imgTemp.setDensity(160);
        Canvas canvas = new Canvas(imgTemp);
        Paint paint = new Paint();
        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawColor(bgColor);

        canvas.drawBitmap(imgTemp, src, dst, paint);

        // 绘制边框
        Paint paintRect = new Paint();
        paintRect.setStyle(Paint.Style.STROKE);
        paintRect.setColor(rectColor);
        paintRect.setStrokeWidth(2);
        canvas.drawLine(0, 0, width, 0, paintRect);
        canvas.drawLine(0, 0, 0, height, paintRect);
        canvas.drawLine(width, 0, width, height, paintRect);
        canvas.drawLine(0, height, width, height, paintRect);

        // 绘制文本
        canvas.drawText(text, 9, height - 8, textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        BitmapDrawable bd = new BitmapDrawable(imgTemp);

        // 返回
        return bd;
    }

    /**
     * 根据给定的参数进行进行四舍五入
     *
     * @param num
     *            要四舍五入的数字
     * @param roundBit
     *            四舍五入位数 正数表示：小数点后位数；负数表示：小数前位数
     * @return 四舍五入后的数字
     */
    public static double round(double num, int roundBit) {
        int piontBit = 1;
        double numtmp = 0.0D;
        if (roundBit < 0) {
            String tmpstr = "1";
            roundBit = Math.abs(roundBit);
            for (int i = 0; i < roundBit; i++) {
                tmpstr = tmpstr + "0";
            }
            piontBit = Integer.parseInt(tmpstr);
            roundBit = 0;
            num /= piontBit;
        }
        BigDecimal b = new BigDecimal(Double.toString(num));
        BigDecimal one = new BigDecimal("1");
        numtmp = b.divide(one, roundBit, 4).doubleValue();
        return numtmp * piontBit;
    }

}