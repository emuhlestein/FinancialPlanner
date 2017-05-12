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
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment used for adding tax deferred income sources.
 *
 * @author Ed Muhlestein
 */
public class EditTaxDeferredIncomeFragment extends Fragment {
    public static final String EDIT_TAXDEF_INCOME_FRAG_TAG = "edit taxdef income frag tag";
    private long mIncomeTypeId;
    private int mIncomeType;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;
    @Bind(R.id.penalty_age_text) EditText mPenaltyAge;
    @Bind(R.id.penalty_amount_text) EditText mPenaltyAmount;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public EditTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static EditTaxDeferredIncomeFragment newInstance(long incomeSourceId) {
        EditTaxDeferredIncomeFragment fragment = new EditTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_edit_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mIncomeTypeId == -1) {
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

    private void updateUI() {
        if(mIncomeTypeId == -1) {
            return;
        }

        TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), mIncomeTypeId);
        if(tdid == null) {
            return;
        }

        String incomeSourceName = tdid.getName();
        mIncomeType = tdid.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), mIncomeType);
        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(tdid.getMonthAddition());
        String interestString = String.valueOf(tdid.getInterest());
        String penaltyAge = tdid.getMinimumAge();
        String penaltyAmount = tdid.getPenalty();

        // TODO finish up  bind data to views
        String balanceString;
        BalanceData[] bd = DataBaseUtils.getBalanceData(getContext(), mIncomeTypeId);
        if(bd == null) {
            balanceString = "0.00";
        } else {
            balanceString = SystemUtils.getFormattedCurrency(bd[0].getBalance());
        }

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setSubtitle(incomeSourceTypeString);
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(interestString);
        mMonthlyIncrease.setText(monthlyIncreaseString);
        mPenaltyAge.setText(penaltyAge);
        mPenaltyAmount.setText(penaltyAmount);
    }

    public void sendIncomeSourceData() {
        String balance = SystemUtils.getCurrencyValue(mBalance);
        String interest = mAnnualInterest.getText().toString();
        String monthlyIncrease = SystemUtils.getCurrencyValue(mMonthlyIncrease);
        if(!SystemUtils.isValidFloatValue(balance)) {
            // TODO pop up error message
            return;
        }
        if(!SystemUtils.isValidFloatValue(interest)) {
            // TODO pop up error message
            return;
        }
        if(!SystemUtils.isValidFloatValue(monthlyIncrease)) {
            // TODO pop up error message
            return;
        }

        Intent returnIntent = new Intent();

        String name = mIncomeSourceName.getText().toString();
        String date = SystemUtils.getTodaysDate();

        String penaltyAmount = mPenaltyAmount.getText().toString();
        String minimumAge = mPenaltyAge.getText().toString();

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mIncomeTypeId);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, mIncomeType);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE_DATE, date);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, interest);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_INCREASE, monthlyIncrease);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_PENALTY_AMOUNT, penaltyAmount);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MINIMUM_AGE, minimumAge);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
