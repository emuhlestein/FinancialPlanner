package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

public class WithdrawPercentFragment extends Fragment {

    public WithdrawPercentFragment() {
        // Required empty public constructor
    }

    public static WithdrawPercentFragment newInstance() {
        WithdrawPercentFragment fragment = new WithdrawPercentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_percent, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
