package com.intelliviz.retirementhelper.ui.income;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.PensionIncomeData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPensionIncomeFragment extends Fragment {
    public static final String EDIT_PENSION_INCOME_FRAG_TAG = "edit pension income frag tag";
    private long mIncomeTypeId;
    private int mIncomeType;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.age_text) EditText mMinAge;
    @Bind(R.id.monthly_amount_text) EditText mMonthlyAmount;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public static EditPensionIncomeFragment newInstance(long incomeTypeId) {
        EditPensionIncomeFragment fragment = new EditPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeTypeId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditPensionIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeTypeId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_pension_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mIncomeTypeId == -1) {
            ab.setSubtitle("Savings");
        } else {
            updateUI();
        }
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        return view;
    }

    private void updateUI() {
        if (mIncomeTypeId == -1) {
            return;
        }

        PensionIncomeData pid = DataBaseUtils.getPensionIncomeData(getContext(), mIncomeTypeId);
        if(pid == null) {
            return;
        }

        String name = pid.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(pid.getMonthlyBenefit());
        String age = pid.getStartAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(age);
        mMonthlyAmount.setText(monthlyBenefit);
    }

    public void sendIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String amount = SystemUtils.getCurrencyValue(mMonthlyAmount);
        if(!SystemUtils.isValidFloatValue(amount)) {
            // TODO pop up error message
            return;
        }

        // TODO need to validate age

        Intent returnIntent = new Intent();

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mIncomeTypeId);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, mIncomeType);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT, amount);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MINIMUM_AGE, age);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();

    }
}
