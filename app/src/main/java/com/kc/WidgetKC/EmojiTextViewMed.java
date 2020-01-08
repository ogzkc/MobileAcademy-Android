package com.kc.WidgetKC;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.kc.Util.Config;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Oguz on 21.05.2016.
 */
public class EmojiTextViewMed extends EmojiconTextView {

    public EmojiTextViewMed(Context context) {
        this(context, null);
    }

    public EmojiTextViewMed(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiTextViewMed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.ubuntuMed));
    }

}
