package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsData;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by edm on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity implements View.OnClickListener{

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
    @Bind(R.id.retirement_parms_cancel) Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dialog_retire_parms);
        ButterKnife.bind(this);

        mZeroBalanceButton.setOnClickListener(this);
        mNoReduceButton.setOnClickListener(this);
        mWithdrawPercentButton.setOnClickListener(this);
        mWithdrawPercent.setEnabled(false);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        Intent intent = getIntent();
        RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIRMENTOPTIONSDATA);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();

                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        updateUI(rod);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.withdraw_percent_button) {
            if (v instanceof RadioButton) {
                RadioButton rb = (RadioButton) v;
                mWithdrawPercent.setEnabled(rb.isChecked());
            }
        } else {
            mWithdrawPercent.setEnabled(false);
        }
    }

    private void updateUI(RetirementOptionsData rod) {
        mStartAgeEditText.setText(rod.getStartAge());
        mEndAgeEditText.setText(rod.getEndAge());
        int mode = rod.getWithdrawMode();
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

        mWithdrawPercent.setText(rod.getWithdrawPercent());
        mIncludeInflationCheckBox.setSelected(rod.getIncludeInflation() == 1);
        mInflationAmountEditText.setText(rod.getInflationAmount());
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

        String inflationAmount;
        int includeInflation = mIncludeInflationCheckBox.isChecked() ? 1 : 0;
        if(includeInflation == 1) {
            inflationAmount = mInflationAmountEditText.getText().toString();
        } else {
            inflationAmount = "0";
        }

        RetirementOptionsData rod = new RetirementOptionsData(startAge, endAge, withdrawMode, withdrawPercent, includeInflation, inflationAmount);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIRMENTOPTIONSDATA, rod);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
