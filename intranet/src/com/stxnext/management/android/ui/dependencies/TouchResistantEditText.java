package com.stxnext.management.android.ui.dependencies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class TouchResistantEditText extends EditText{

    public TouchResistantEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchResistantEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchResistantEditText(Context context) {
        super(context);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    
}