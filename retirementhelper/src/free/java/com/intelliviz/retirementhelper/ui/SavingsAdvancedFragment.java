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
public class SavingsAdvancedFragment extends Fragment {

    public SavingsAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_savings_advanced, container, false);
    }

    public void setMonthlyAddition(String monthlyAddition) {
    }

    public String getMonthlyAddition() {
        return "0";
    }

    public void setStopMonthlyAdditionAge(String stopMonthlyAdditionAge) {
    }

    public String getStopMonthlyAdditionAge() {
        return "0";
    }

    public void setAnnualPercentIncrease(String age) {
    }

    public String getAnnualPercentIncrease() {
        return "0";
    }

    public void setShowMonths(boolean checked) {
    }

    public boolean getShowMonths() {
        return false;
    }
}
