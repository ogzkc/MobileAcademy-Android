package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kc.Util.Config;

/**
 * Created by Oguz on 21.02.2016.
 */
public class TextViewBold extends TextView {

    public TextViewBold(Context context) {
        this(context, null);
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuBold));
    }



}
