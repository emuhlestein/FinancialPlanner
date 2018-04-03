package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoAdvancedFragment extends Fragment {


    public PersonalInfoAdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_info_advanced, container, false);
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
    }

    public void setIncludeSpouse(boolean includeSpouse) {
    }

    public boolean getIncludeSpouse() {
        return false;
    }

    public void setSpouseBirthdate(String birthdate) {
    }

    public String getSpouseBirthdate() {
        return "";
    }
}
