package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewIncomeSourceFragment extends Fragment {
    public static final String VIEW_INCOME_FRAG_TAG = "view income frag tag";

    public static ViewIncomeSourceFragment newInstance() {
        ViewIncomeSourceFragment fragment = new ViewIncomeSourceFragment();
        return fragment;
    }

    public ViewIncomeSourceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_income_source, container, false);
    }

}
