package com.godavarisandroid.mystore.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.godavarisandroid.mystore.R;


@SuppressLint("AppCompatCustomView")
public class OpensansEditText extends EditText {

    public OpensansEditText(Context context) {
        super(context);
    }

    public OpensansEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, R.attr.font_type_opensans);
    }

    public OpensansEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        if (isInEditMode())
            return;
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OpensansTextView);

            String str = a.getString(R.styleable.OpensansTextView_font_type_opensans);
            if (str == null)
                str = "1";
            a.recycle();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

