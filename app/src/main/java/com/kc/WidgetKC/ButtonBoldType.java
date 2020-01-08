package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.kc.Util.Config;

/**
 * Created by Oguz on 29.02.2016.
 */
public class ButtonBoldType extends Button {



    public ButtonBoldType(Context context) {
        super(context);
    }

    public ButtonBoldType(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuBold));
    }

    public ButtonBoldType(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuBold));
    }



}
