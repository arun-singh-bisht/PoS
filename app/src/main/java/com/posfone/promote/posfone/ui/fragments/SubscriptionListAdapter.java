package com.posfone.promote.posfone.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.data.local.models.PaymentModel;
import com.posfone.promote.posfone.data.local.models.SubscriptionModel;

import java.util.List;

public class SubscriptionListAdapter  extends BaseAdapter {

    private Context context;
    private List<SubscriptionModel> subscriptionModelList;
    public SubscriptionListAdapter(Context context, List<SubscriptionModel> subscriptionModelList)
    {
        this.context = context;
        this.subscriptionModelList = subscriptionModelList;
    }


    @Override
    public int getCount() {
        return subscriptionModelList.size();
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
            rowView = inflater.inflate(R.layout.subscription_list, null);
            TextView package_name = rowView.findViewById(R.id.package_name);
            TextView status_btn = rowView.findViewById(R.id.btn_active_plan);
            TextView txn_id = rowView.findViewById(R.id.txn_id);
            TextView start_date = rowView.findViewById(R.id.start_date);
            TextView due_date = rowView.findViewById(R.id.due_date);
            TextView payment_date = rowView.findViewById(R.id.payment_date);
            package_name.setText(subscriptionModelList.get(i).getPackage_name());
            if(!subscriptionModelList.get(i).getStatus().equals("Success")) {
                //status_btn.setBackground(rowView.getResources().getDrawable(R.drawable.round_inactive_color));
            }
            status_btn.setText(subscriptionModelList.get(i).getStatus());
            start_date.setText(subscriptionModelList.get(i).getStart_date());
            txn_id.setText(subscriptionModelList.get(i).getTransaction_id());
            due_date.setText(subscriptionModelList.get(i).getEnd_date());
            payment_date.setText(subscriptionModelList.get(i).getEntry_date());

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
