package com.posfone.promote.posfone.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.posfone.promote.posfone.R;

public class FilterBottomDialogFragment extends BottomSheetDialogFragment {

    EditText ed_search_by_number;
    EditText ed_search_by_area_code;
    TextView txt_selectedCountry;
    View view;
    View tv_apply_filter;
    View tv_clear_filter;
    FilterBottomDialogFragmentInterface mInterface;

    private String number;
    private String areaCode;
    private String countryName;

    public interface FilterBottomDialogFragmentInterface
    {
        void onFilterShowCountryList();
        void onFliterApplyClick();
        void onFilterClearClick();
    }


    public static FilterBottomDialogFragment newInstance(FilterBottomDialogFragmentInterface mInterface) {
        FilterBottomDialogFragment filterBottomDialogFragment = new FilterBottomDialogFragment();
        filterBottomDialogFragment.setInterface(mInterface);
        return filterBottomDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.bottom_sheet_layout, container,
                false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        ed_search_by_number = view.findViewById(R.id.ed_search_by_number);
        ed_search_by_area_code = view.findViewById(R.id.ed_search_by_area_code);
        tv_apply_filter = view.findViewById(R.id.tv_apply_filter);
        tv_clear_filter = view.findViewById(R.id.tv_clear_filter);
        txt_selectedCountry = view.findViewById(R.id.txt_selectedCountry);
        tv_apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onFliterApplyClick();
            }
        });
        tv_clear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onFilterClearClick();
            }
        });
        txt_selectedCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onFilterShowCountryList();
            }
        });

        ed_search_by_number.setText(number);
        ed_search_by_area_code.setText(areaCode);
        txt_selectedCountry.setText(countryName);
    }

    private void setInterface(FilterBottomDialogFragmentInterface mInterface)
    {
        this.mInterface = mInterface;
    }

    public String getInput_ContactNumber()
    {
        return ed_search_by_number.getText().toString();
    }

    public String getInput_AreaCode()
    {
        return ed_search_by_area_code.getText().toString();
    }

    public void setInput_ContactNumber(String number)
    {
        this.number = number;
    }

    public void setInput_AreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public void setInput_CountryName(String countryName)
    {
        this.countryName = countryName;
        if(view!=null)
        {
            txt_selectedCountry.setText(countryName);
        }
    }
}
