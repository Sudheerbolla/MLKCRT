package com.godavarisandroid.mystore.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;


public class OpensansTextView extends TextView {
    private int font;

    public OpensansTextView(Context context) {
        super(context);
    }

    public OpensansTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//         initFont(context, attrs);
        initView(context, attrs);
    }

    public OpensansTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // initFont(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OpensansTextView);
            String str = a.getString(R.styleable.OpensansTextView_font_type_opensans);
            if (str == null)
                str = "1";
            switch (Integer.parseInt(str)) {
                case 1:
                    str = "fonts/OpenSans-Light.ttf";
                    break;
                case 2:
                    str = "fonts/OpenSans-Regular.ttf";
                    break;
                case 3:
                    str = "fonts/OpenSans-Semibold.ttf";
                    break;
                case 4:
                    str = "fonts/OpenSans-Bold.ttf";
                    break;
                case 5:
                    str = "fonts/OpenSans-ExtraBold.ttf";
                    break;
                default:
                    str = "fonts/OpenSans-Regular.ttf";
                    break;
            }
            setTypeface(FontManager.getInstance(getContext()).loadFont(str));
            a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}