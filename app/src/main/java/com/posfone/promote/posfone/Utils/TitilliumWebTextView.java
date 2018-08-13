package com.posfone.promote.posfone.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitilliumWebTextView extends TextView {

    public TitilliumWebTextView(Context context) {
        super(context);
        init();
    }

    public TitilliumWebTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitilliumWebTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/TitilliumWeb_Regular.ttf");
            setTypeface(tf);
    }
}
