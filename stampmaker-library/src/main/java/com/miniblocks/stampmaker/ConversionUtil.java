package com.miniblocks.stampmaker;

import android.content.Context;
import android.util.DisplayMetrics;

public class ConversionUtil {
    public static int convertDpToPixel(int dp, Context context){
        return dp * (context.getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int convertPixelToDp(int pixels, Context context){
        return pixels/(context.getResources().getDisplayMetrics().densityDpi/DisplayMetrics.DENSITY_DEFAULT);
    }
}
