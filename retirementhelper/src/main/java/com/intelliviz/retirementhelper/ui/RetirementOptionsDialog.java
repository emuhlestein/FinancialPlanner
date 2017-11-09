package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog for gathering user-selected retirement options.
 * Created by Ed Muhlestein on 5/15/2017.
 */

public class RetirementOptionsDialog extends AppCompatActivity implements View.OnClickListener{
    private static final String DEFAULT_END_AGE = "90";
    private static final int DEFAULT_WITHDRAW_MODE = RetirementConstants.WITHDRAW_MODE_PERCENT;
    private static final String DEFAULT_WITHDRAW_AMOUNT = "4";
    private RetirementOptionsData mROD;

    @Bind(R.id.end_age_edit_text)
    EditText mEndAgeEditText;

    @Bind(R.id.withdraw_amount_button)
    RadioButton mWithdrawAmountButton;

    @Bind(R.id.withdraw_percent_button)
    RadioButton mWithdrawPercentButton;

    @Bind(R.id.input_withdraw_percent)
    android.support.design.widget.TextInputLayout mInputWithdrawPercentage;

    @Bind(R.id.input_withdraw_amount)
    android.support.design.widget.TextInputLayout mInputWithdrawAmount;

    @Bind(R.id.withdraw_amount_edit_text)
    TextView mWithdrawAmountTextView;

    @Bind(R.id.withdraw_percent_edit_text)
    TextView mWithdrawPercentTextView;

    @Bind(R.id.withdraw_mode_radio_group)
    RadioGroup mWithdrawModeRadioGroup;

    @Bind(R.id.retirement_parms_ok)
    Button mOk;

    @Bind(R.id.retirement_parms_cancel)
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

        mWithdrawAmountButton.setOnClickListener(this);
        mWithdrawPercentButton.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mROD = b.getParcelable(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);

        updateUI();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.withdraw_percent_button) {
            mInputWithdrawPercentage.setVisibility(View.GONE);
            mInputWithdrawAmount.setVisibility(View.VISIBLE);
            mWithdrawPercentTextView.setText(mROD.getWithdrawAmount());
        } else {
            mInputWithdrawAmount.setVisibility(View.GONE);
            mInputWithdrawPercentage.setVisibility(View.VISIBLE);
            mWithdrawAmountTextView.setText(mROD.getWithdrawAmount());
        }
    }

    private void updateUI() {

        int mode = DEFAULT_WITHDRAW_MODE;
        String ageString = DEFAULT_END_AGE;
        String withDrawAmount = DEFAULT_WITHDRAW_AMOUNT;
        if(mROD != null) {
            mode = mROD.getWithdrawMode();
            String endAge = mROD.getEndAge();
            AgeData age = SystemUtils.parseAgeString(endAge);
            if(age != null) {
                int year = age.getYear();
                ageString = Integer.toString(year);
            }
            withDrawAmount = mROD.getWithdrawAmount();
        }

        mEndAgeEditText.setText(ageString);
        switch(mode) {
            case RetirementConstants.WITHDRAW_MODE_AMOUNT:
                mWithdrawModeRadioGroup.check(mWithdrawAmountButton.getId());
                mWithdrawAmountTextView.setText(withDrawAmount);
                mInputWithdrawAmount.setVisibility(View.VISIBLE);
                mInputWithdrawPercentage.setVisibility(View.GONE);
                break;
            case RetirementConstants.WITHDRAW_MODE_PERCENT:
                mWithdrawModeRadioGroup.check(mWithdrawPercentButton.getId());
                mWithdrawPercentTextView.setText(withDrawAmount);
                mInputWithdrawAmount.setVisibility(View.GONE);
                mInputWithdrawPercentage.setVisibility(View.VISIBLE);
                break;
            default:
                mWithdrawModeRadioGroup.check(mWithdrawAmountButton.getId());
                mWithdrawAmountTextView.setText(withDrawAmount);
                mInputWithdrawAmount.setVisibility(View.GONE);
                mInputWithdrawPercentage.setVisibility(View.VISIBLE);
        }
    }

    private void sendData() {
        String endAge = mEndAgeEditText.getText().toString();
        int withdrawMode;
        String withdrawAmount;
        switch(mWithdrawModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.withdraw_amount_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_AMOUNT;
                withdrawAmount = mWithdrawAmountTextView.getText().toString();
                break;
            case R.id.withdraw_percent_button:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_PERCENT;
                withdrawAmount = mWithdrawPercentTextView.getText().toString();
                break;
            default:
                withdrawMode = RetirementConstants.WITHDRAW_MODE_AMOUNT;
                withdrawAmount = mWithdrawAmountTextView.getText().toString();
        }

        if(withdrawAmount.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(RetirementOptionsDialog.this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.withdraw_requires_value));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        withdrawAmount = SystemUtils.getFloatValue(withdrawAmount);
        if(withdrawAmount == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(RetirementOptionsDialog.this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.invalid_widthdraw_amount));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        AgeData age = SystemUtils.parseAgeString(endAge, "0");
        if(age == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(RetirementOptionsDialog.this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.age_invalid));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        String birthdate = mROD.getBirthdate();
        AgeData nowAge = SystemUtils.getAge(birthdate);
        if(age.isBefore(nowAge)) {
            String message = getString(R.string.age_greater_thna_your_age) + nowAge.toString();
            AlertDialog alertDialog = new AlertDialog.Builder(RetirementOptionsDialog.this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }
        endAge = age.getUnformattedString();

        RetirementOptionsData rod = new RetirementOptionsData(endAge, withdrawMode, withdrawAmount, mROD.getBirthdate());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
