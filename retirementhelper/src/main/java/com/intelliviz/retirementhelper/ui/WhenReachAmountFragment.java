package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WhenReachAmountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhenReachAmountFragment extends Fragment {

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

}
