package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.kc.Util.Config;

/**
 * Created by Oguz on 29.02.2016.
 */
public class ButtonMedType extends Button {



    public ButtonMedType(Context context) {
        super(context);
    }

    public ButtonMedType(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuMed));
    }

    public ButtonMedType(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuMed));
    }



}
