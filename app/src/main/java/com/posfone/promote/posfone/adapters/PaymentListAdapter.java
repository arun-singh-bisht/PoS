package com.posfone.promote.posfone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.model.PaymentModel;

import java.util.List;

public class PaymentListAdapter extends BaseAdapter {

    private Context context;
    private  List<PaymentModel> paymentModelList;
    public PaymentListAdapter(Context context, List<PaymentModel> paymentModelList)
    {
        this.context = context;
        this.paymentModelList = paymentModelList;
    }


    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.payment_list_row, null);
            // configure view holder

        }

        return rowView;



    }

    class ViewHolder
    {
        TextView textView;
        TextView textView2;
    }

}
