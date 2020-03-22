package com.miniblocks.stampmaker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.icu.util.Measure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Stamp extends View implements View.OnClickListener {
    private static StampStateChangeListener stateChangeListener;
    private int strokeWidth = 8; // default width
    private int strokeFillColor = Color.rgb(30, 144, 255);
    public static final boolean STATE_FILL = true;
    public static final boolean STATE_NOT_FILL = false;
    private Paint pathPaint;
    private Paint pathStrokePaint;
    private Path stampPath;
    private int width = 80; // in dp -default width and height
    private int height = 80; // in dp -default width and height
    private boolean fill = false;

    public Stamp(Context context) {
        super(context);

        setOnClickListener(this);
        init();

    }

    public Stamp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
//        stateChangeListener = (StampStateChangeListener)context;
        setUpAttributes(attrs);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        System.out.println(width+" "+height);
        createPath();
        canvas.drawPath(stampPath, pathStrokePaint);
        if(fill){
            canvas.drawPath(stampPath, pathPaint);
        }
//        if(fill){
//            stampPath.close();
//        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = ConversionUtil.convertDpToPixel(width, getContext())+getSuggestedMinimumWidth()+getPaddingLeft()+getPaddingRight();
        int desiredHeight = ConversionUtil.convertDpToPixel(height, getContext())+getSuggestedMinimumHeight()+getPaddingBottom()+getPaddingTop();
        setMeasuredDimension(width = measureDimension(desiredWidth, widthMeasureSpec),
                height = measureDimension(desiredHeight, heightMeasureSpec));

    }


    private int measureDimension(int desiredSize, int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = desiredSize;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }

        }
        return result;
    }
    private void init(){
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pathPaint.setColor(strokeFillColor);
        pathPaint.setStrokeWidth(strokeWidth);
        pathStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathStrokePaint.setStyle(Paint.Style.STROKE);
        pathStrokePaint.setColor(strokeFillColor);
        pathStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        pathStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        pathStrokePaint.setStrokeWidth(strokeWidth);
        stampPath = new Path();
    }

    private void createPath(){
        /*our shape is like
         * |     |
         * |  _  |
         * |_| |_|*/
        stampPath.moveTo(strokeWidth, 0);
        stampPath.lineTo(strokeWidth, height-strokeWidth);
        float min = Math.min(width, height)/2.0f;
        stampPath.lineTo(min, min);
        stampPath.lineTo(width-strokeWidth, height-strokeWidth);
        stampPath.lineTo(width-strokeWidth, 0);

    }
    private void setUpAttributes(AttributeSet attrs){
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.Stamp, 0, 0);
        try{
            strokeWidth = array.getInt(R.styleable.Stamp_stamp_strokeWidth, strokeWidth);
            strokeFillColor = array.getColor(R.styleable.Stamp_stamp_strokeFillColor, strokeFillColor);
            fill = array.getBoolean(R.styleable.Stamp_stamp_fill, fill);
        }finally {
            array.recycle();
        }

    }

    public void changeState(boolean changeTo){
        if(fill != changeTo){
            fill = changeTo;
            invalidate();
        }
        if(stateChangeListener != null){
            stateChangeListener.onStateChange(fill);
        }

    }

    public boolean getState(){
        return fill;
    }

    public void setOnStampStateChangeListener(StampStateChangeListener listener){
        stateChangeListener = listener;
    }


    @Override
    public void onClick(View v) {
        fill = !fill;
        if(stateChangeListener != null){
//            stateChangeListener = (StampStateChangeListener)mContext;
            stateChangeListener.onStateChange(fill);
        }


        invalidate();



    }
}
