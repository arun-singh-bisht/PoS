package com.posfone.promote.posfone.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.posfone.promote.posfone.ChooseNumberActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.model.TwilioNumber;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class NumberFragment extends BaseFragment{

    String fragmentName;
    private View view;
    private List<TwilioNumber> twilioNumberList;
    private GenericListAdapter genericListAdapter = null;

    private ChooseNumberActivity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ChooseNumberActivity)activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("NumberFragment",fragmentName+" Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("NumberFragment",fragmentName+" Fragment onCreateView");

        view = inflater.inflate(R.layout.fragment_choose_number, container, false);

        initViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("NumberFragment",fragmentName+" Fragment onResume");
        loadTwilioNumberList(twilioNumberList);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("NumberFragment",fragmentName+" Fragment onPause");
    }

    public void setFragmentName(String name)
    {
        this.fragmentName = name;
    }

    private void initViews()
    {
        EditText ed_search =  view.findViewById(R.id.ed_search);
        ed_search.setHint("Search number");

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<TwilioNumber> filterdList = TwilioNumber.getFilterdList(twilioNumberList, s+"");
                loadTwilioNumberList(filterdList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void setData(List<TwilioNumber> twilioNumberList)
    {
        this.twilioNumberList = twilioNumberList;
        loadTwilioNumberList(twilioNumberList);
    }

    private void loadTwilioNumberList(final List<TwilioNumber> list)
    {
        if(view==null)
            return;

        if(list==null || list.size()==0)
            return;

        genericListAdapter = new GenericListAdapter(getActivity(),list.size(),R.layout.number_fragment_type_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_number =  view.findViewById(R.id.txt_number);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                //TextView txt_number_type =  view.findViewById(R.id.txt_number_type);

                final TwilioNumber twilioNumber = list.get(position);
                txt_number.setText(twilioNumber.phone_number);
                //txt_number_type.setText(twilioNumber.type.toUpperCase());


                txt_country_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String selectedTwilioNumber = twilioNumber.phone_number;
                        String selectedTwilioNumberType = twilioNumber.type;

                        try {
                            activity.selectTwilioNumber(selectedTwilioNumber,selectedTwilioNumberType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                return view;
            }
        };

        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setAdapter(genericListAdapter);
    }

}
