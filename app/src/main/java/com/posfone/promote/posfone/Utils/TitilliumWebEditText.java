package com.posfone.promote.posfone.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class TitilliumWebEditText extends EditText {

    public TitilliumWebEditText(Context context) {
        super(context);
        init();
    }

    public TitilliumWebEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitilliumWebEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/TitilliumWeb_Regular.ttf");
            setTypeface(tf);
    }
}
