package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

public class WhenReachPercentIncomeFragment extends Fragment {

    public WhenReachPercentIncomeFragment() {
        // Required empty public constructor
    }

    public static WhenReachPercentIncomeFragment newInstance() {
        WhenReachPercentIncomeFragment fragment = new WhenReachPercentIncomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reach_percent_income, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
