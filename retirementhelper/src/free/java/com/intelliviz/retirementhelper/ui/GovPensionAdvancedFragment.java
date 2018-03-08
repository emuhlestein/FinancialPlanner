package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GovPensionAdvancedFragment extends Fragment {

    public GovPensionAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gov_pension_advanced, container, false);
    }


    public void setStartRetirementAge(String age) {
    }

    public String getStartRetirementAge() {
        return "";
    }
}
