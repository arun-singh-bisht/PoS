package com.posfone.promote.posfone.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.posfone.promote.posfone.R;

/**
 * Created by Arun.Singh on 9/6/2018.
 */

public class CustomAlertDialog {

    public interface I_CustomAlertDialog
    {
        void onPositiveClick();
        void onNegativeClick();
    }

    public void showDialog(Activity activity, String msg,I_CustomAlertDialog i_customAlertDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialo);

       TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
