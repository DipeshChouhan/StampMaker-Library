package com.miniblocks.stampmaker;

import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miniblocks.stampmaker.utils.ConversionUtil;
import com.miniblocks.stampmaker.utils.StampHelper;
import com.miniblocks.stampmaker.utils.StampStateChangeListener;

public class SwitchStamp extends View implements View.OnTouchListener, StampHelper {
    private int width = 15; //in dp
    private int height = 10;    // in dp
    public static final boolean SWITCH_ON = true;
    public static final boolean SWITCH_OFF = false;
    private int strokeWidth;
    private int radius;
    private int cx;
    private int cy;
    private int onColor;
    private int offColor;
    private int strokeColor;
    private boolean switchOn;
    private int roundCornerRadius;
    private Paint strokePaint;
    private Paint fillPaint;
    private int offsetX;
    private int endX;
    private int defaultColor;
    private ValueAnimator animator;
    private float[] startTempXY = new float[2];
    private float[] endTempXY = new float[2];
    private int defaultValue = 0;
    private ValueAnimator colorAnimator;
    private static StampStateChangeListener stateChangeListener;
    public SwitchStamp(Context context) {
        super(context);
        setOnTouchListener(this);
        init();
    }
    public SwitchStamp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        setUpAttributes(attrs);
        init();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = ConversionUtil.convertDpToPixel(width, getContext())+getSuggestedMinimumWidth()
                + getPaddingLeft() + getPaddingRight();
        int desiredHeight = ConversionUtil.convertDpToPixel(height, getContext())+getSuggestedMinimumHeight()
                +getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width = ConversionUtil.measureDimension(desiredWidth, widthMeasureSpec),
                height = ConversionUtil.measureDimension(desiredHeight, heightMeasureSpec));
        radius = Math.min(width, height)/4 -strokeWidth;
        offsetX = strokeWidth+radius+10;

        cy = height/2;
        endX = width - 2*(offsetX)-10;
        cx = switchOn?endX+offsetX:radius+10;

    }

    private float[] startComputeXY(int pos, int pRadius){
        float[] result = startTempXY;
        double startAngle = 0;
        double angle = startAngle + (pos*Math.PI/4);
        result[0] = (float)(pRadius*Math.cos(angle)+cx);
        result[1] = (float)(pRadius*Math.sin(angle)+cy);
        return result;
    }

    private float[] endComputeXY(int pos, int pRadius){
        float[] result = endTempXY;
        double startAngle =0;
        double angle = startAngle + (pos*Math.PI/4);
        result[0] = (float)(pRadius*Math.cos(angle) + cx) ;
        result[1] = (float)(pRadius*Math.sin(angle) + cy);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fillPaint.setColor(defaultColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(0, 0, width, height,roundCornerRadius,
                    roundCornerRadius, strokePaint);
        }else{
            canvas.drawRect(0, 0, width, height, strokePaint);
        }
        canvas.drawCircle(cx, cy, radius, fillPaint);
        for(int i = 0; i < defaultValue; i++){
            float[] sXY = startComputeXY(i, offsetX-10);
            float[] eXY = endComputeXY(i, offsetX);
            canvas.drawLine(sXY[0], sXY[1], eXY[0], eXY[1], strokePaint);
        }
    }

    private void init(){
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(strokeColor);
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

    }
    private void setUpAttributes(AttributeSet attrs){
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.SwitchStamp, 0, 0);
        try{
            strokeWidth = array.getInt(R.styleable.SwitchStamp_switch_strokeWidth, 4);
            onColor = array.getColor(R.styleable.SwitchStamp_switch_onFillColor, Color.YELLOW);
            offColor = array.getColor(R.styleable.SwitchStamp_switch_offFillColor, Color.GRAY);
            strokeColor = array.getColor(R.styleable.SwitchStamp_switch_strokeColor, Color.GRAY);
            switchOn = array.getBoolean(R.styleable.SwitchStamp_switch_on, false);
            roundCornerRadius = array.getInt(R.styleable.SwitchStamp_switch_cornerRadius, 16);
            if(switchOn){
                defaultColor = onColor;
                defaultValue = 8;
            }else{
                defaultColor = offColor;
            }
        }finally {
            array.recycle();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_DOWN){
            PropertyValuesHolder animatorHolder = PropertyValuesHolder.ofInt("ANIMATOR", offsetX, offsetX+endX);
            PropertyValuesHolder linesAnimatorHolder = PropertyValuesHolder.ofInt("LINES_ANIMATOR", 0, 8);
            animator = new ValueAnimator();
            animator.setValues(animatorHolder, linesAnimatorHolder);
            colorAnimator = ValueAnimator.ofInt(offColor, onColor);
            colorAnimator.setEvaluator(new ArgbEvaluator());
            colorAnimator.setDuration(400);
            animator.setDuration(400);

            colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    defaultColor = (int)colorAnimator.getAnimatedValue();

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    cx = (int)animator.getAnimatedValue("ANIMATOR");
                    defaultValue = (int)animation.getAnimatedValue("LINES_ANIMATOR");
                    invalidate();
                }
            });
            if(switchOn){
                animator.reverse();
                colorAnimator.reverse();

            }else{
                animator.start();
                colorAnimator.start();
            }
            switchOn = !switchOn;
            if(stateChangeListener != null){
                stateChangeListener.onStateChange(switchOn);
            }
            return true;
        }

        return false;
    }

    public void setOnStateChangeListener(@NonNull StampStateChangeListener listener){
        stateChangeListener = listener;
    }

    @Override
    public void setSize(int newWidth, int newHeight) {
        width = ConversionUtil.convertDpToPixel(newWidth, getContext());
        height = ConversionUtil.convertDpToPixel(newHeight, getContext());
        invalidate();
    }


    public void setStrokeWidth(int strWidth){
        strokeWidth = strWidth;
        invalidate();
    }
    public void setOnColor(int onClr){
        onColor = onClr;
        invalidate();
    }
    public void setOffColor(int offClr){
        offColor = offClr;
        invalidate();
    }
    public void setCornerRadius(int cornerRadius){
        roundCornerRadius = cornerRadius;
        invalidate();
    }
    public void setStrokeColor(int strClr){
        strokeColor = strClr;
        invalidate();
    }

    public int getStrokeWidth(){
        return strokeWidth;
    }
    public int getCornerRadius(){
        return roundCornerRadius;
    }
    public int getOnColor(){
        return onColor;
    }
    public int getOffColor(){
        return offColor;
    }
    public int getStrokeColor(){
        return strokeColor;
    }

    public boolean getState(){
        return switchOn;
    }
    public void changeState(boolean changeTo){
        if(switchOn != changeTo){
            switchOn = changeTo;
            invalidate();
            if(stateChangeListener != null){
                stateChangeListener.onStateChange(switchOn);
            }
        }

    }
}
