package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPensionIncomeFragment extends Fragment {


    public static EditPensionIncomeFragment newInstance(long incomeSourceId) {
        EditPensionIncomeFragment fragment = new EditPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditPensionIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_pension_income, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
