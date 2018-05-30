package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.util.AgeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GovPensionAdvancedFragment extends Fragment {
    @BindView(R.id.start_age_text_view)
    TextView mStartRetirementAge;

    @OnClick(R.id.edit_start_age_button) void editAge() {
        AgeData startAge;
        String age = mStartRetirementAge.getText().toString();
        String trimmedAge = AgeUtils.trimAge(age);
        startAge = AgeUtils.parseAgeString(trimmedAge);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

    public GovPensionAdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gov_pension_advanced, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void setStartRetirementAge(String age) {
        mStartRetirementAge.setText(age);
    }

    public String getStartRetirementAge() {
        return mStartRetirementAge.getText().toString();
    }
}
