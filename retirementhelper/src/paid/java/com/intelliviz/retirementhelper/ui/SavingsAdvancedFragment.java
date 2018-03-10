package com.intelliviz.retirementhelper.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsAdvancedFragment extends Fragment implements AgeDialog.OnAgeEditListener {

    @BindView(R.id.monthly_addition_text)
    EditText mMonthlyAddition;

    @BindView(R.id.stop_age_text_view)
    TextView mStopMonthlyAdditionAgeTextView;

    @BindView(R.id.annual_percent_increase_edit_text)
    EditText mAnnualPercentIncrease;

    @BindView(R.id.show_months_check_box)
    CheckBox mShowMonths;

    @OnClick(R.id.edit_stop_age_button) void editStopMonthlyAdditionAge() {
        AgeData stopAge;
        String age = mStopMonthlyAdditionAgeTextView.getText().toString();
        String trimmedAge = SystemUtils.trimAge(age);
        stopAge = SystemUtils.parseAgeString(trimmedAge);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+stopAge.getYear(), ""+stopAge.getMonth());
        dialog.show(fm, "");
        dialog.setTargetFragment(this, 0);
    }

    public SavingsAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_savings_advanced, container, false);
        ButterKnife.bind(this, view);

        mMonthlyAddition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyAddition.setText(formattedString);
                    }
                }
            }
        });

        mAnnualPercentIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualPercentIncrease.setText(interest);
                    } else {
                        mAnnualPercentIncrease.setText("");
                    }
                }
            }
        });

        return view;
    }

    public void setMonthlyAddition(String monthlyAddition) {
        mMonthlyAddition.setText(monthlyAddition);
    }

    public String getMonthlyAddition() {
        return mMonthlyAddition.getText().toString();
    }

    public void setStopMonthlyAdditionAge(String age) {
        mStopMonthlyAdditionAgeTextView.setText(age);
    }

    public String getStopMonthlyAdditionAge() {
        return mStopMonthlyAdditionAgeTextView.getText().toString();
    }

    public void setAnnualPercentIncrease(String age) {
        mAnnualPercentIncrease.setText(age);
    }

    public String getAnnualPercentIncrease() {
        return mAnnualPercentIncrease.getText().toString();
    }

    public void setShowMonths(boolean checked) {
        mShowMonths.setChecked(checked);
    }

    public boolean getShowMonths() {
        return mShowMonths.isChecked();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mStopMonthlyAdditionAgeTextView.setText(age.toString());
    }
}
