package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;


public class ViewTaxDeferredIncomeFragment extends Fragment {
    public static final String TAXDEF_VIEW_INCOME_FRAG_TAG = "taxdef view income frag tag";
    private long mIncomeSourceId;
    private int mIncomeSourceType;


    public ViewTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static ViewTaxDeferredIncomeFragment newInstance(long incomeSourceId) {
        ViewTaxDeferredIncomeFragment fragment = new ViewTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeSourceId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_tax_deferred_income, container, false);
    }

}
