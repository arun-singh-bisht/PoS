package com.posfone.promote.posfone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.posfone.promote.posfone.MainActivity;
import com.posfone.promote.posfone.PackageActivity;
import com.posfone.promote.posfone.PackageDetailActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.SignInActivity;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.model.BaseModel;
import com.posfone.promote.posfone.model.CountryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class NumberFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    String fragmentName;
    private View view;

    private  String TAB_NAME ="";
    public static String TAB_COUNTRY ="tab_country";
    public static String TAB_AREA ="tab_area";
    public static String TAB_TYPE ="tab_type";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("PaymentFragment",fragmentName+"Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("PaymentFragment",fragmentName+"Fragment onCreateView");

        view = inflater.inflate(R.layout.fragment_choose_number, container, false);

        initViews();

        return view;
    }


    private void initViews()
    {

        EditText ed_search =  view.findViewById(R.id.ed_search);

        GenericListAdapter genericListAdapter = null;
        if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
        {
            ed_search.setHint("Search country");

            List<String> countries_array = Arrays.asList(getResources().getStringArray(R.array.countries_array));

            final List<BaseModel> countryList = new ArrayList<BaseModel>();

            for(String country: countries_array)
            {
                CountryModel countryModel = new CountryModel();
                countryModel.setName(country);
                countryModel.setPhonecode("+61");
                countryList.add(countryModel);
            }

            genericListAdapter = new GenericListAdapter(getActivity(),countryList,R.layout.number_fragment_country_row){

                @Override
                public View initGenericView(View view, int position) {

                    TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                    TextView txt_country_code =  view.findViewById(R.id.txt_country_code);

                    CountryModel countryModel = (CountryModel)countryList.get(position);
                    txt_country_name.setText(countryModel.getName());
                    txt_country_code.setText(countryModel.getPhonecode());

                    return view;
                }
            };
        }else if(TAB_NAME.equalsIgnoreCase(TAB_AREA))
        {
            ed_search.setHint("Search area");

            List<String> india_states = Arrays.asList(getResources().getStringArray(R.array.india_states));

            final List<BaseModel> countryList = new ArrayList<BaseModel>();

            for(String country: india_states)
            {
                CountryModel countryModel = new CountryModel();
                countryModel.setName(country);
                countryModel.setPhonecode("+61");
                countryList.add(countryModel);
            }

            genericListAdapter = new GenericListAdapter(getActivity(),countryList,R.layout.number_fragment_country_row){

                @Override
                public View initGenericView(View view, int position) {

                    TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                    TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                    ImageView img_flag =  view.findViewById(R.id.img_flag);


                    CountryModel countryModel = (CountryModel)countryList.get(position);
                    txt_country_name.setText(countryModel.getName());
                    //txt_country_code.setText(countryModel.getCountryCode());
                    img_flag.setImageResource(R.drawable.ind_flag);

                    return view;
                }
            };
        }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
        {

            ed_search.setHint("Search type");

            final List<BaseModel> countryList = new ArrayList<>();
            for(int i = 0;i<15;i++)
            {
                CountryModel countryModel = new CountryModel();
                countryModel.setName("+44 7447 301897");
                countryList.add(countryModel);
            }

            genericListAdapter = new GenericListAdapter(getActivity(),countryList,R.layout.number_fragment_type_row){

                @Override
                public View initGenericView(View view, int position) {

                    TextView txt_number =  view.findViewById(R.id.txt_number);
                    TextView txt_country_code =  view.findViewById(R.id.txt_country_code);

                    CountryModel countryModel = (CountryModel)countryList.get(position);
                    txt_number.setText(countryModel.getName());

                    txt_country_code.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(),PackageActivity.class));
                        }
                    });

                    return view;
                }
            };
        }



        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
        listView.setAdapter(genericListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void setTabName(String tab_name)
    {
        TAB_NAME = tab_name;
    }

}
