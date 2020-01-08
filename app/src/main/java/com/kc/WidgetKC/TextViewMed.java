package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kc.Util.Config;

/**
 * Created by Oguz on 21.02.2016.
 */
public class TextViewMed extends TextView {

    public TextViewMed(Context context) {
        this(context, null);
    }

    public TextViewMed(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewMed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuMed));
    }



}
