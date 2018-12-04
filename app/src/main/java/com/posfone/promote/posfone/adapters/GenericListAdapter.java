package com.posfone.promote.posfone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.VoiceActivity;
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
       // if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layoutId, null);
            // configure view holder

      //  }

        rowView =  initGenericView(rowView,i);
        ImageView imageView = null;
         try {
             imageView=rowView.findViewById(R.id.call_button);
         }catch (Exception e){}

        if(imageView!=null) {
            final View finalRowView = rowView;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView view1 = finalRowView.findViewById(R.id.contact_number);
                    TextView view2 = finalRowView.findViewById(R.id.contact_name);
                    String number = view1.getText().toString();
                    String name = view2.getText().toString();
                    Intent intent = new Intent(context,VoiceActivity.class);
                    intent.putExtra("from_number","16617480240");
                    intent.putExtra("to_number",number);
                    intent.putExtra("to_name",name);
                    context.startActivity(intent);
                }
            });
        }
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
