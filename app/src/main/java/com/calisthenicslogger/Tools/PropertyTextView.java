package com.calisthenicslogger.Tools;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PropertyTextView extends TextView {

    public String exerciseName;

    public PropertyTextView(Context context) {
        super(context);
    }

    public PropertyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PropertyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
