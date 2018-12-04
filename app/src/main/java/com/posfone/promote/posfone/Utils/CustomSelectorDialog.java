package com.posfone.promote.posfone.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posfone.promote.posfone.R;

import java.util.List;

public class CustomSelectorDialog {

    public static void showDialog(Activity context, String title, List<Item> itemList,final I_CustomSelectorDialog  i_customSelectorDialog)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_selector_dialog_layout);
        TextView txt_title =  dialog.findViewById(R.id.txt_title);
        txt_title.setText(title);
        LinearLayout linearLayout = dialog.findViewById(R.id.lay_item);

        for(final Item item:itemList)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout,null);
            TextView txt_item =  view.findViewById(R.id.txt_item);
            txt_item.setText(item.itemName);

            if(item.isSelected)
                txt_item.setTextColor(ContextCompat.getColor(context, R.color.color_selected_iteam));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i_customSelectorDialog.onItemSelected(item);
                    dialog.dismiss();
                }
            });
            linearLayout.addView(view);
        }

        dialog.show();
    }
    public static class Item
    {
        public String itemName;
        public String itemName_alias;
        public boolean isSelected;
    }

    public interface I_CustomSelectorDialog
    {
        void onItemSelected(Item item);
    }
}
