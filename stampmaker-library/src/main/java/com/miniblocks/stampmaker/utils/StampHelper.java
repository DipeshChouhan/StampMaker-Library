package com.miniblocks.stampmaker.utils;

public interface StampHelper {
    void changeState(boolean changeTo);
    void setOnStateChangeListener(StampStateChangeListener stateChangeListener);
    void setSize(int newWidth, int newHeight);
    void setStrokeWidth(int strWidth);
    int getStrokeWidth();
    boolean getState();
    void setStrokeColor(int outerClr);
    int getStrokeColor();
}
