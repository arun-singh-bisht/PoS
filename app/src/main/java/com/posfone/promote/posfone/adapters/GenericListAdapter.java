package com.posfone.promote.posfone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.model.BaseModel;
import com.posfone.promote.posfone.model.PaymentModel;

import java.util.List;

public class GenericListAdapter extends BaseAdapter {

    private Context context;
    private  List<BaseModel> objectList;
    private int layoutId,totalRecords;
    public GenericListAdapter(Context context, List<BaseModel> objectList, int layoutId)
    {
        this.context = context;
        this.objectList = objectList;
        this.layoutId = layoutId;
    }

    public GenericListAdapter(Context context, int totalRecords, int layoutId)
    {
        this.context = context;
        this.layoutId = layoutId;
        this.totalRecords =totalRecords;
    }

    public GenericListAdapter(Context context)
    {
        this.context = context;
    }


    @Override
    public int getCount() {
        if(objectList==null)
            return totalRecords;

        return objectList.size();
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
            rowView = inflater.inflate(layoutId, null);
            // configure view holder

        }

        rowView =  initGenericView(rowView,i);

        return rowView;
    }

    public View initGenericView(View view,int position)
    {
        return view;
    }


    public void setListSize(int size)
    {
        this.totalRecords = size;
    }

    public void setView(int resId)
    {
        this.layoutId = resId;
    }

}
