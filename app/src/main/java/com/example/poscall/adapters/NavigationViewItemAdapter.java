package com.example.poscall.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poscall.R;


import java.util.List;

public class NavigationViewItemAdapter extends BaseAdapter {


    private Context context;
    private List<NavigationViewItemModel> navigationViewItemModelList;


    public NavigationViewItemAdapter(Context context,List<NavigationViewItemModel> navigationViewItemModelList)
    {
        this.context= context;
        this.navigationViewItemModelList=navigationViewItemModelList;
    }


    @Override
    public int getCount() {
        return navigationViewItemModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return navigationViewItemModelList.get(i);
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
            rowView = inflater.inflate(R.layout.menu_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)rowView.findViewById(R.id.menu_item_icon);
            viewHolder.textView = (TextView)rowView.findViewById(R.id.menu_item_text);
            rowView.setTag(viewHolder);
        }
        ViewHolder  viewHolder = (ViewHolder)rowView.getTag();

        //ViewHolder viewHolder = (ViewHolder)rowView.getTag();
        viewHolder.imageView.setImageResource(navigationViewItemModelList.get(i).res_icon);
        viewHolder.textView.setText(navigationViewItemModelList.get(i).item_name);

        return rowView;
    }

    class ViewHolder
    {
        ImageView imageView;
        TextView textView;
        TextView textView2;
    }


    public static class NavigationViewItemModel
    {
        public int res_icon;
        public String item_name;
        public String tag;
    }

}
