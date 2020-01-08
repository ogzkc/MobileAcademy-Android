package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kc.Util.Config;

/**
 * Created by Oguz on 21.02.2016.
 */
public class MenuKCTextView extends TextView {

    public MenuKCTextView(Context context) {
        this(context, null);
    }

    public MenuKCTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuKCTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuBold));
    }

    public void makeSelected(){
        setTextColor(Color.parseColor("#30d1d5"));
    }

    public void makeUnselected(){
        setTextColor(Color.WHITE);
    }


}
