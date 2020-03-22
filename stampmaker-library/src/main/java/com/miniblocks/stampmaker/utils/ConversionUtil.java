package com.miniblocks.stampmaker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class ConversionUtil {
    public static int convertDpToPixel(int dp, Context context){
        return dp * (context.getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int convertPixelToDp(int pixels, Context context){
        return pixels/(context.getResources().getDisplayMetrics().densityDpi/DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int measureDimension(int desiredSize, int measureSpec){
        int result;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if(specMode == View.MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = desiredSize;
            if(specMode == View.MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
