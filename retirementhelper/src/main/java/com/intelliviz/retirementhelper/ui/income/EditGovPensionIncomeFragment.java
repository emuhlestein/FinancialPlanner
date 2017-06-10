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
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGovPensionIncomeFragment extends Fragment {
    public static final String EDIT_GOVPENSION_INCOME_FRAG_TAG = "edit govpension income frag tag";
    private long mIncomeId;
    private int mIncomeType;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.input_layout_min_age) EditText mMinAge;
    @Bind(R.id.monthly_amount_text) EditText mMonthlyAmount;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;


    public static EditGovPensionIncomeFragment newInstance(long incomeSourceId) {
        EditGovPensionIncomeFragment fragment = new EditGovPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditGovPensionIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_gov_pension_income, container, false);
        ButterKnife.bind(this, view);
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mIncomeId == -1) {
            ab.setSubtitle(SystemUtils.getIncomeSourceTypeString(getContext(), RetirementConstants.INCOME_TYPE_TAX_DEFERRED));
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
    }

    private void updateUI() {
        if(mIncomeId == -1) {
            return;
        }
        GovPensionIncomeData gpid = DataBaseUtils.getGovPensionIncomeData(getContext(), mIncomeId);
        if(gpid == null) {
            return;
        }
        mIncomeType = gpid.getType();

        mIncomeSourceName.setText(gpid.getName());
        mMinAge.setText(gpid.getStartAge());
        mMonthlyAmount.setText(SystemUtils.getFormattedCurrency(gpid.getMonthlyBenefit(0)));
    }

    private void sendIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String monthlyAmount = mMonthlyAmount.getText().toString();

        Intent returnIntent = new Intent();

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mIncomeId);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, mIncomeType);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_BENEFIT, monthlyAmount);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MINIMUM_AGE, age);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
