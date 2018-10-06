package com.intelliviz.income.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.income.R;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.ui.NewAgeDialog;
import com.intelliviz.lowlevel.util.SystemUtils;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MONTH;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_YEAR;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsAdvancedFragment extends Fragment implements NewAgeDialog.OnAgeEditListener {

    private EditText mMonthlyAddition;
    private TextView mStopMonthlyAdditionAgeTextView;
    private EditText mAnnualPercentIncrease;
    private CheckBox mShowMonths;

    public SavingsAdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_savings_advanced, container, false);

        mMonthlyAddition = view.findViewById(R.id.monthly_addition_text);
        mStopMonthlyAdditionAgeTextView = view.findViewById(R.id.stop_age_text_view);
        mAnnualPercentIncrease = view.findViewById(R.id.annual_percent_increase_edit_text);
        mShowMonths = view.findViewById(R.id.show_months_check_box);
        Button editStopAgeButton = view.findViewById(R.id.edit_stop_age_button);
        editStopAgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeData stopAge;
                String age = mStopMonthlyAdditionAgeTextView.getText().toString();
                stopAge = new AgeData(age);
                if(getActivity() != null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    NewAgeDialog dialog = NewAgeDialog.newInstance(0, "" + stopAge.getYear(), "" + stopAge.getMonth());
                    dialog.show(fm, "");
                    dialog.setTargetFragment(SavingsAdvancedFragment.this, 0);
                }
            }
        });

        mMonthlyAddition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = SystemUtils.getFloatValue(str);
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
                    interest = SystemUtils.getFloatValue(interest);
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK){
            String month = intent.getStringExtra(EXTRA_MONTH);
            String year = intent.getStringExtra(EXTRA_YEAR);
            AgeData age = new AgeData(year, month);
            mStopMonthlyAdditionAgeTextView.setText(age.toString());
        }
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
    public void onEditAge(int id, String year, String month) {
        AgeData age = new AgeData(year, month);
        mStopMonthlyAdditionAgeTextView.setText(age.toString());
    }
}
