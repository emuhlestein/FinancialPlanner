package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

public class WithdrawAmountFragment extends Fragment {

    public WithdrawAmountFragment() {
        // Required empty public constructor
    }

    public static WithdrawAmountFragment newInstance() {
        WithdrawAmountFragment fragment = new WithdrawAmountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_amount, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
