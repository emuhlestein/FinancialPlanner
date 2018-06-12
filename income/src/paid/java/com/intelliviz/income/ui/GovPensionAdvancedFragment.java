package com.intelliviz.income.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intelliviz.income.R;
import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.util.AgeUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class GovPensionAdvancedFragment extends Fragment {
    private TextView mStartRetirementAge;
    private Button mEditStartAgeButton;

    public GovPensionAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gov_pension_advanced, container, false);
        mStartRetirementAge = view.findViewById(R.id.start_age_text_view);
        mEditStartAgeButton = view.findViewById(R.id.edit_start_age_button);
        mEditStartAgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeData startAge;
                String age = mStartRetirementAge.getText().toString();
                String trimmedAge = AgeUtils.trimAge(age);
                startAge = AgeUtils.parseAgeString(trimmedAge);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
                dialog.show(fm, "");
            }
        });
        return view;
    }

    public void setStartRetirementAge(String age) {
        mStartRetirementAge.setText(age);
    }

    public String getStartRetirementAge() {
        return mStartRetirementAge.getText().toString();
    }
}
