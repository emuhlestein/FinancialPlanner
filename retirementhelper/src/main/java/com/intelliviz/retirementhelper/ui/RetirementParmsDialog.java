package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementParmsData;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by edm on 5/15/2017.
 */

public class RetirementParmsDialog extends DialogFragment implements View.OnClickListener{
    private static final String ARG_START_AGE = "start date";
    private static final String ARG_END_AGE = "end date";

    @Bind(R.id.start_age_edit_text) EditText mStartAgeEditText;
    @Bind(R.id.end_age_edit_text) EditText mEndAgeEditText;
    @Bind(R.id.zero_balance_button) RadioButton mZeroBalanceButton;
    @Bind(R.id.no_reduce_button) RadioButton mNoReduceButton;
    @Bind(R.id.withdraw_percent_button) RadioButton mWithdrawPercentButton;
    @Bind(R.id.withdraw_percent_edit_text) EditText mWithdrawPercent;
    @Bind(R.id.apply_inflation_checkbox) CheckBox mIncludeInflationCheckBox;
    @Bind(R.id.inflation_amount_edit_text) EditText mInflationAmountEditText;
    @Bind(R.id.withdraw_mode_radio_group) RadioGroup mWithdrawModeRadioGroup;
    @Bind(R.id.retirement_parms_ok) Button mOk;

    public static RetirementParmsDialog newInstance(String startAge, String endAge) {
        Bundle args = new Bundle();
        args.putString(ARG_START_AGE, startAge);
        args.putString(ARG_END_AGE, endAge);

        RetirementParmsDialog fragment = new RetirementParmsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_gov_pension_income, container, false);
        ButterKnife.bind(this, view);

        mZeroBalanceButton.setOnClickListener(this);
        mNoReduceButton.setOnClickListener(this);
        mWithdrawPercentButton.setOnClickListener(this);

        updateUI();

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.withdraw_percent_button) {
            mInflationAmountEditText.setEnabled(true);
        } else {
            mInflationAmountEditText.setEnabled(false);
        }
    }

    private void updateUI() {
        RetirementParmsData rpd = DataBaseUtils.getRetirementParmsData(getContext());
        if(rpd == null) {
            return;
        }
        mStartAgeEditText.setText(rpd.getStartAge());
        mEndAgeEditText.setText(rpd.getEndAge());
        int mode = rpd.getWithdrawMode();
        switch(mode) {
            case RetirementConstants.WITHDRAW_MODE_ZERO_PRI:
                mWithdrawModeRadioGroup.check(mZeroBalanceButton.getId());
                break;
            case RetirementConstants.WITHDRAW_MODE_NO_REDUC:
                mWithdrawModeRadioGroup.check(mNoReduceButton.getId());
                break;
            case RetirementConstants.WITHDRAW_MODE_PERCENT:
                mWithdrawModeRadioGroup.check(mWithdrawPercentButton.getId());
                break;
            default:
                mWithdrawModeRadioGroup.check(mZeroBalanceButton.getId());
        }

        mWithdrawPercent.setText(rpd.getWithdrawPercent());
        mIncludeInflationCheckBox.setSelected(rpd.getIncludeInflation() == 1 ? true : false);
        mInflationAmountEditText.setText(rpd.getInflationAmount());
    }

    private void sendIncomeSourceData() {
        String startAge = mStartAgeEditText.getText().toString();
        String endAge = mEndAgeEditText.getText().toString();
        int withdrawMode;
        switch(mWithdrawModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.zero_balance_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_ZERO_PRI;
                break;
            case R.id.no_reduce_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_NO_REDUC;
                break;
            case R.id.withdraw_percent_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_PERCENT;
                break;
            default:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_ZERO_PRI;
        }

        String withdrawPercent = "0";
        if(withdrawMode == RetirementConstants.WITHDRAW_MODE_PERCENT) {
            withdrawPercent = mWithdrawPercent.getText().toString();
        }

        int inludeInflation;
        String inflationAmount;
        if(mIncludeInflationCheckBox.isChecked()) {
            inludeInflation = 1;
            inflationAmount = mInflationAmountEditText.getText().toString();
        } else {
            inludeInflation = 0;
            inflationAmount = "0";
        }


        Intent returnIntent = new Intent();

        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_START_AGE, startAge);
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_END_AGE, endAge);
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_WITHDRAW_MODE, withdrawMode);
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_WITHDRAW_PERCENT, withdrawPercent);
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_INCLUDE_INFLAT, inludeInflation);
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRE_PARMS_INFLAT_AMOUNT, inflationAmount);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

}
