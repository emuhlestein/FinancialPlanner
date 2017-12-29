package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.RetirementOptionsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Dialog for gathering user-selected retirement options.
 * Created by Ed Muhlestein on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity implements View.OnClickListener {
    private RetirementOptionsEntity mROE;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.reach_amount_layout)
    LinearLayout mReachAmountLayout;

    @BindView(R.id.reach_percent_income_layout)
    LinearLayout mReachPercentIncomeLayout;

    @BindView(R.id.income_summary_layout)
    LinearLayout mIncomeSummaryLayout;

    @BindView(R.id.end_age_edit_text)
    EditText mEndAgeEditText;

    @BindView(R.id.reach_amount_edit_text)
    EditText mReachAmountEditText;

    @BindView(R.id.reach_percent_income_edit_text)
    EditText mReachPercentIncomeEditText;

    @BindView(R.id.annual_income_edit_text)
    EditText mAnnualIncomeEditText;

    @BindView(R.id.reach_amount_button)
    Button mReachAmountButton;

    @BindView(R.id.reach_percent_income_button)
    Button mReachPercentIncomeButton;

    @BindView(R.id.income_summary_button)
    Button mIncomeSummaryButton;

    @BindView(R.id.retirement_options_radio_group)
    RadioGroup mWithdrawModeRadioGroup;

    @BindView(R.id.retirement_parms_ok)
    Button mOk;

    @BindView(R.id.retirement_parms_cancel)
    Button mCancel;

    @OnClick(R.id.retirement_parms_ok) void onClickOk() {
        sendData();
    }

    @OnClick(R.id.retirement_parms_cancel) void onClickCancel() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_retire_parms);
        ButterKnife.bind(this);

        mReachAmountButton.setOnClickListener(this);
        mReachPercentIncomeButton.setOnClickListener(this);
        mIncomeSummaryButton.setOnClickListener(this);

        RetirementOptionsViewModel mViewModel;
        mViewModel = ViewModelProviders.of(this).
                get(RetirementOptionsViewModel.class);

        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                updateUI(roe);
                mROE = roe;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int mode = getCurrentMode();
        setLayoutVisibilty(mode);
    }

    private void updateUI(RetirementOptionsEntity roe) {

        int mode = roe.getCurrentOption();
        switch(mode) {
            case RetirementConstants.REACH_AMOUNT_MODE:
                mWithdrawModeRadioGroup.check(mReachAmountButton.getId());
                break;
            case RetirementConstants.REACH_IMCOME_PERCENT_MODE:
                mWithdrawModeRadioGroup.check(mReachPercentIncomeButton.getId());
                break;
            case RetirementConstants.INCOME_SUMMARY_MODE:
                mWithdrawModeRadioGroup.check(mIncomeSummaryButton.getId());
                break;
        }

        mReachAmountEditText.setText(SystemUtils.getFormattedCurrency(roe.getReachAmount()));
        mReachPercentIncomeEditText.setText(roe.getReachPercent() + "%");
        mEndAgeEditText.setText(roe.getEndAge().toString());
        mAnnualIncomeEditText.setText(SystemUtils.getFormattedCurrency(roe.getAnnualIncome()));

        setLayoutVisibilty(mode);
    }

    private void sendData() {

        Intent returnIntent = new Intent();
        String value;
        String floatValue;

        value = mReachAmountEditText.getText().toString();
        floatValue = getFloatValue(value);
        if(floatValue == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_REACH_AMOUNT, floatValue);

        value = mReachPercentIncomeEditText.getText().toString();
        floatValue = getFloatValue(value);
        if(floatValue == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_REACH_INCOME_PERCENT, floatValue);

        value = mAnnualIncomeEditText.getText().toString();
        floatValue = getFloatValue(value);
        if(floatValue == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_ANNUAL_INCOME, floatValue);

        value = mEndAgeEditText.getText().toString();
        value = SystemUtils.trimAge(value);
        AgeData endAge = SystemUtils.parseAgeString(value);
        if(endAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREMENT_INCOME_SUMMARY_AGE, endAge);

        returnIntent.putExtra(RetirementConstants.RETIREMENT_MODE, getCurrentMode());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    private void setLayoutVisibilty(int mode) {
        switch(mode) {
            case RetirementConstants.REACH_AMOUNT_MODE:
                mReachAmountLayout.setVisibility(View.VISIBLE);
                mReachPercentIncomeLayout.setVisibility(View.GONE);
                mIncomeSummaryLayout.setVisibility(View.GONE);
                break;
            case RetirementConstants.REACH_IMCOME_PERCENT_MODE:
                mReachAmountLayout.setVisibility(View.GONE);
                mReachPercentIncomeLayout.setVisibility(View.VISIBLE);
                mIncomeSummaryLayout.setVisibility(View.GONE);
                break;
            case RetirementConstants.INCOME_SUMMARY_MODE:
                mReachAmountLayout.setVisibility(View.GONE);
                mReachPercentIncomeLayout.setVisibility(View.GONE);
                mIncomeSummaryLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private int getCurrentMode() {
        int selectedId = mWithdrawModeRadioGroup.getCheckedRadioButtonId();
        if(mReachAmountButton.getId() == selectedId) {
            return RetirementConstants.REACH_AMOUNT_MODE;
        } else if(mReachPercentIncomeButton.getId() == selectedId) {
            return RetirementConstants.REACH_IMCOME_PERCENT_MODE;
        } else if(mIncomeSummaryButton.getId() == selectedId) {
            return RetirementConstants.INCOME_SUMMARY_MODE;
        } else {
            return RetirementConstants.UNKNOWN_MODE;
        }
    }
}
