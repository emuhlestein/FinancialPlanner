package com.intelliviz.retirementhelper.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.viewmodel.RetirementOptionsViewModel;

import butterknife.ButterKnife;

public class WhenReachAmountFragment extends Fragment {

    private RetirementOptionsViewModel mViewModel;

    public WhenReachAmountFragment() {
        // Required empty public constructor
    }

    public static WhenReachAmountFragment newInstance() {
        WhenReachAmountFragment fragment = new WhenReachAmountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reach_amount, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(RetirementOptionsViewModel.class);
        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                updateUI(roe);
            }
        });
    }

    private void updateUI(RetirementOptionsEntity roe) {

    }
}
