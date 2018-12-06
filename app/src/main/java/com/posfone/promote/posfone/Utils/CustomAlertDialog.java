package com.posfone.promote.posfone.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

    public interface I_CustomInputDialog
    {
        void onPositiveClick(String text);
        void onNegativeClick();
    }

    public static void showDialog(Activity activity, String msg,int res_id,final I_CustomAlertDialog i_customAlertDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

       TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onNegativeClick();
            }
        });
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick();
            }
        });
        dialog.show();
    }


    public static void showDialog(Activity activity, String msg,String positiveBtnText,String negativeBtnText,int res_id,final I_CustomAlertDialog i_customAlertDialog){

        final Dialog dialog = new Dialog(activity,R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

        //Set Title
        TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        //Set Negative button Text and click listener
        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        if(negativeBtnText!=null)
            txt_negative.setText(negativeBtnText);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onNegativeClick();
            }
        });
        //Set Positive button Text and click listener
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        if(positiveBtnText!=null)
            txt_positive.setText(positiveBtnText);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick();
            }
        });
        dialog.show();
    }


    public static void showDialogSingleButton(Activity activity,String msg,final I_CustomAlertDialog i_customAlertDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialo_single_button);

        TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        TextView txt_ok = (TextView) dialog.findViewById(R.id.txt_ok);
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick();
            }
        });
        dialog.show();
    }


    public static void showInputDialog(Activity activity, String msg,int res_id,final I_CustomInputDialog i_customInputDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

        TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        final EditText editText = dialog.findViewById(R.id.edit_name);


        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customInputDialog.onNegativeClick();
            }
        });
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = editText.getText().toString();
                if(s.isEmpty())
                    return;
                dialog.dismiss();
                i_customInputDialog.onPositiveClick(s);
            }
        });
        dialog.show();
    }
}
