package com.posfone.promote.posfone.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitilliumWebBoldTextView extends TextView {

    public TitilliumWebBoldTextView(Context context) {
        super(context);
        init();
    }

    public TitilliumWebBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitilliumWebBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/TitilliumWeb_Bold.ttf");
            setTypeface(tf);
    }
}
