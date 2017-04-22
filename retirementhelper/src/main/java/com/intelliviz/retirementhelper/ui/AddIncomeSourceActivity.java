package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

public class AddIncomeSourceActivity extends AppCompatActivity {
    public static final String INCOME_TYPE = "income_type";
    public static final String INSTITUTE_NAME = "institute_name";
    public static final String BALANCE = "balance";
    public static final String INTEREST = "interest";
    public static final String MONTHLY_INCREASE = "monthly_increase";
    public static final String MONTHLY_BENEFIT = "monthly_benefit";
    public static final String START_AGE = "start_age";
    private int mIncomeSourceType;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_source);

        Intent intent = getIntent();
        mIncomeSourceType = intent.getIntExtra(INCOME_TYPE, 0);
        switch(mIncomeSourceType) {
            case RetirementConstants.INCOME_TYPE_SAVINGS:
                setContentView(R.layout.activity_add_savings_source);
                break;
            case RetirementConstants.INCOME_TYPE_PENSION:
                setContentView(R.layout.activity_add_pension_source);
                break;
            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                setContentView(R.layout.activity_add_gov_pension_source);
                break;
            default:
                mIncomeSourceType = RetirementConstants.INCOME_TYPE_SAVINGS;
                setContentView(R.layout.activity_add_savings_source);
        }

        mToolbar = (Toolbar) findViewById(R.id.add_income_source_toolbar);

        setSupportActionBar(mToolbar);
        String []incomeTypes = getResources().getStringArray(R.array.income_types);
        String incomeSourceType = incomeTypes[mIncomeSourceType];
        mToolbar.setSubtitle(incomeSourceType);
    }

    public void addIncomeSource(View view) {
        Intent returnIntent = new Intent();

        EditText instituteName = (EditText) findViewById(R.id.name_edit_text);
        String name = instituteName.getText().toString();

        returnIntent.putExtra(INSTITUTE_NAME, name);
        returnIntent.putExtra(INCOME_TYPE, mIncomeSourceType);

        switch(mIncomeSourceType) {
            case RetirementConstants.INCOME_TYPE_SAVINGS:
                EditText balanceEditText = (EditText) findViewById(R.id.balance_text);
                EditText interestEditText = (EditText) findViewById(R.id.annual_interest_text);
                EditText monthlyIncreaseEditText = (EditText) findViewById(R.id.monthly_increase_text);
                returnIntent.putExtra(BALANCE, balanceEditText.toString());
                returnIntent.putExtra(INTEREST, interestEditText.toString());
                returnIntent.putExtra(MONTHLY_INCREASE, monthlyIncreaseEditText.toString());
                break;
            case RetirementConstants.INCOME_TYPE_PENSION:
                EditText startAgeEditText = (EditText) findViewById(R.id.start_age_edit_text);
                EditText monthlyBenefitEditText = (EditText) findViewById(R.id.monthly_benefit_text);
                returnIntent.putExtra(START_AGE, startAgeEditText.toString());
                returnIntent.putExtra(MONTHLY_BENEFIT, monthlyBenefitEditText.toString());
                break;
            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                break;
            default:
                setResult(Activity.RESULT_CANCELED);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
