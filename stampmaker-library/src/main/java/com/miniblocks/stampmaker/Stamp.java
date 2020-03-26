package com.miniblocks.stampmaker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.miniblocks.stampmaker.utils.ConversionUtil;
import com.miniblocks.stampmaker.utils.StampHelper;
import com.miniblocks.stampmaker.utils.StampStateChangeListener;

public class Stamp extends View implements StampHelper {

    private int strokeWidth = 8; // default width
    private int strokeFillColor = Color.rgb(30, 144, 255);
    private int strokeColor = Color.GRAY;
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
        setSaveEnabled(true);
        init();

    }

    public Stamp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
//        stateChangeListener = (StampStateChangeListener)context;
        setUpAttributes(attrs);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
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
        setMeasuredDimension(width = ConversionUtil.measureDimension(desiredWidth, widthMeasureSpec),
                height = ConversionUtil.measureDimension(desiredHeight, heightMeasureSpec));

    }


    private void init(){
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pathPaint.setColor(strokeFillColor);
        pathPaint.setStrokeWidth(strokeWidth);
        pathStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathStrokePaint.setStyle(Paint.Style.STROKE);
        pathStrokePaint.setColor(strokeColor);
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
        stampPath.close();

    }
    private void setUpAttributes(AttributeSet attrs){
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.Stamp, 0, 0);
        try{
            strokeWidth = array.getInt(R.styleable.Stamp_stamp_strokeWidth, strokeWidth);
            strokeFillColor = array.getColor(R.styleable.Stamp_stamp_strokeFillColor, strokeFillColor);
            fill = array.getBoolean(R.styleable.Stamp_stamp_fill, fill);
            strokeColor = array.getColor(R.styleable.Stamp_stamp_strokeColor, strokeColor);
        }finally {
            array.recycle();
        }

    }

    public void changeState(boolean changeTo){
        if(fill != changeTo){
            fill = changeTo;
            invalidate();
        }

    }



    public void setStrokeColor(int color){
        strokeColor = color;
        invalidate();
    }

    public int getStrokeColor(){
        return strokeColor;
    }

    public void setFillColor(int color){
        strokeFillColor = color;
        invalidate();
    }
    public int getFillColor(){
        return strokeFillColor;
    }

    public void setStrokeWidth(int strWidth){
        strokeWidth = strWidth;
        invalidate();
    }
    public int getStrokeWidth(){
        return strokeWidth;
    }
    // in dp
    public void setSize(int newWidth, int newHeight){
        width = ConversionUtil.convertDpToPixel(newWidth, getContext());
        height = ConversionUtil.convertDpToPixel(newHeight, getContext());
        invalidate();
    }
    public boolean getState(){
        return fill;
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState =  super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.mFill = fill ? 1: 0;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState myState = (SavedState)state;
        super.onRestoreInstanceState(state);
        fill = myState.mFill == 1;

        invalidate();
    }

    private static class SavedState extends BaseSavedState {
        int mFill;

        public SavedState(Parcel source) {
            super(source);
            mFill = source.readInt();

        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mFill);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
