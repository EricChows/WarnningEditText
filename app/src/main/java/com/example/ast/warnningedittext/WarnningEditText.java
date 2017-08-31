package com.example.ast.warnningedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by xiaoniu on 2017/8/28.
 */

public class WarnningEditText extends android.support.v7.widget.AppCompatEditText {

    private final String TAG = getClass().getName();
    //View宽
    private int width;
    //View高
    private int height;
    //默认边框色
    private int borderColor = Color.GRAY;
    //输入正确边框色
    private int correctBorderColor = Color.GREEN;
    //输入错误边框色
    private int warinningBorderColor = Color.RED;
    //边框宽
    private int borderWidth = 2;
    //边框圆角半径
    private int angleRadius = 5;
    //正则表达式
    private String regex = null;
    //边框画笔
    private Paint borderPaint;
    //边框矩形
    private RectF r;
    //正则匹配监听器
    private OnInputMatchListener onInputMatchListener;

    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    public WarnningEditText(Context context) {
        this(context, null);
    }

    public WarnningEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public WarnningEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WarnningEditText);
        borderColor = ta.getColor(R.styleable.WarnningEditText_borderColor, Color.GRAY);
        correctBorderColor = ta.getColor(R.styleable.WarnningEditText_correctBorderColor, Color.GREEN);
        warinningBorderColor = ta.getColor(R.styleable.WarnningEditText_warinningBorderColor, Color.RED);
        borderWidth = ta.getDimensionPixelSize(R.styleable.WarnningEditText_borderWidth, 2);
        angleRadius = ta.getDimensionPixelSize(R.styleable.WarnningEditText_angleRadius, 5);
        ta.recycle();
        init();
    }

    private void init() {
        this.setBackground(null);

        //设置图片线条的抗锯齿
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter
                (0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //初始化边框画笔
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp2px(borderWidth));
        borderPaint.setColor(borderColor);
        //设置文本垂直居中
        this.setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //确定View宽高
        width = w;
        height = h;
        //边框的坐标范围,24是补偿，不加的话边框会向上溢出
        r = new RectF(dp2px(borderWidth), dp2px(borderWidth), width - dp2px(borderWidth), height - dp2px(borderWidth));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //确定View宽高
        width = getWidth();
        height = getHeight();
        //边框的坐标范围,24是补偿，不加的话边框会向上溢出
        r = new RectF(dp2px(borderWidth), dp2px(borderWidth), width - dp2px(borderWidth), height - dp2px(borderWidth));
        //设置画布绘图无锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        drawBorder(canvas);
    }

    /**
     * 绘制边框
     */
    private void drawBorder(Canvas canvas) {
        canvas.save();
        canvas.drawRoundRect(r, dp2px(angleRadius), dp2px(angleRadius), borderPaint);//绘制圆角矩形
        canvas.restore();
    }

    /**
     * 设置正则表达式
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * 输入错误
     */
    public void inputError() {
        this.setText("");
        borderPaint.setColor(warinningBorderColor);
        postInvalidate();
        this.startAnimation(shakeAnimation(5));
    }

    /**
     * 焦点变化
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            borderPaint.setColor(borderColor);
            postInvalidate();
        } else {
            if (!this.getText().toString().equals("")) {
                if (regex != null) {
                    if (!this.getText().toString().matches(regex)) {
                        inputError();
                        onInputMatchListener.onInputWrong();
                        Log.v(TAG, "wrong input");
                    } else {
                        borderPaint.setColor(correctBorderColor);
                        postInvalidate();
                        onInputMatchListener.onInputCorrect();
                        Log.v(TAG, "good input");
                    }
                } else {
                    Log.e(TAG, "no regex");
                }
            } else {
                Log.e(TAG, "no input");
            }
        }
    }

    /**
     * 设置监听器
     *
     * @param onInputMatchListener 正则匹配监听器
     */
    public void setOnInputMatchListener(OnInputMatchListener onInputMatchListener) {
        this.onInputMatchListener = onInputMatchListener;
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    /**
     * dp2px
     */
    public float dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return values * density + 0.5f;
    }

    public interface OnInputMatchListener {
        void onInputWrong();

        void onInputCorrect();
    }
}
