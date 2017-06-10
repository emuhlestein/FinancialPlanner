package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog for gathering user-selected retirement options.
 * Created by edm on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.start_age_edit_text) EditText mStartAgeEditText;
    @Bind(R.id.end_age_edit_text) EditText mEndAgeEditText;
    @Bind(R.id.withdraw_amount_button) RadioButton mWithdrawAmountButton;
    @Bind(R.id.withdraw_percent_button) RadioButton mWithdrawPercentButton;
    @Bind(R.id.amount_text_view) TextView mAmountTextView;
    @Bind(R.id.withdraw_mode_radio_group) RadioGroup mWithdrawModeRadioGroup;
    @Bind(R.id.withdraw_percent_edit_text) EditText mWithdrawAmount;
    @Bind(R.id.retirement_parms_ok) Button mOk;
    @Bind(R.id.retirement_parms_cancel) Button mCancel;
    @OnClick(R.id.retirement_parms_ok) void onClickOk() {
        sendData();
    }
    @OnClick(R.id.retirement_parms_cancel) void onClickCancel() {
        Intent returnIntent = new Intent();

        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_retire_parms);
        ButterKnife.bind(this);

        mWithdrawAmountButton.setOnClickListener(this);
        mWithdrawPercentButton.setOnClickListener(this);

        updateUI();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.withdraw_percent_button) {
            mAmountTextView.setText(R.string.percent_amount);
        } else {
            mAmountTextView.setText(R.string.dollar_amount);
        }
    }

    /**
     * Update the UI.
     */
    private void updateUI() {
        Intent intent = getIntent();
        RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);

        // TODO add check in case rod is null
        mStartAgeEditText.setText(rod.getStartAge());
        mEndAgeEditText.setText(rod.getEndAge());
        int mode = rod.getWithdrawMode();
        switch(mode) {
            case RetirementConstants.WITHDRAW_MODE_AMOUNT:
                mWithdrawModeRadioGroup.check(mWithdrawAmountButton.getId());
                mAmountTextView.setText(R.string.dollar_amount);
                break;
            case RetirementConstants.WITHDRAW_MODE_PERCENT:
                mWithdrawModeRadioGroup.check(mWithdrawPercentButton.getId());
                mAmountTextView.setText(R.string.percent_amount);
                break;
            default:
                mWithdrawModeRadioGroup.check(mWithdrawAmountButton.getId());
                mAmountTextView.setText(R.string.dollar_amount);
        }

        mWithdrawAmount.setText(rod.getWithdrawAmount());
    }

    /**
     * Send the data to the interested party.
     */
    private void sendData() {
        String startAge = mStartAgeEditText.getText().toString();
        String endAge = mEndAgeEditText.getText().toString();
        int withdrawMode;
        switch(mWithdrawModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.withdraw_amount_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_AMOUNT;
                break;
            case R.id.withdraw_percent_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_PERCENT;
                break;
            default:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_AMOUNT;
        }

        // TODO need to validate
        String withdrawAmount = mWithdrawAmount.getText().toString();

        RetirementOptionsData rod = new RetirementOptionsData(startAge, endAge, withdrawMode, withdrawAmount);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
