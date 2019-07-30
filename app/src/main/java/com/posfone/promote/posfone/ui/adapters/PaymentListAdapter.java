package com.posfone.promote.posfone.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.data.local.models.PaymentModel;

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
            TextView twilio_number= rowView.findViewById(R.id.twilio_no);
            TextView txn_id = rowView.findViewById(R.id.txn_id);
            TextView txn_date = rowView.findViewById(R.id.txn_date);
            TextView txn_amount = rowView.findViewById(R.id.payment_amount);
            twilio_number.setText("");
            txn_id.setText("");
            txn_date.setText("");
            txn_amount.setText("");
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
