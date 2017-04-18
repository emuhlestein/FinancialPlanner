package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddIncomeSourceActivity extends AppCompatActivity {
    public static final String INCOME_TYPE = "income_type";
    public static final String INSTITUTE_NAME = "institute_name";
    public static final String BALANCE = "balance";
    public static final String INTEREST = "interest";
    public static final String MONTHLY_INCREASE = "monthly_increase";
    private String mIncomeSourceType;


    @Bind(R.id.add_income_source_toolbar) Toolbar mToolbar;
    @Bind(R.id.name_edit_text) EditText mInstituteName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_source);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int incomeType = intent.getIntExtra(INCOME_TYPE, 0);

        setSupportActionBar(mToolbar);
        String []incomeTypes = getResources().getStringArray(R.array.income_types);
        mIncomeSourceType = incomeTypes[incomeType];
        mToolbar.setSubtitle(mIncomeSourceType);
    }

    public void addIncomeSource(View view) {
        Intent returnIntent = new Intent();

        String name = mInstituteName.getText().toString();
        String balance = mBalance.getText().toString();
        String interest = mAnnualInterest.getText().toString();
        String increase = mMonthlyIncrease.getText().toString();

        returnIntent.putExtra(INCOME_TYPE, mIncomeSourceType);
        returnIntent.putExtra(INSTITUTE_NAME, name);
        returnIntent.putExtra(BALANCE, balance);
        returnIntent.putExtra(INTEREST, interest);
        returnIntent.putExtra(MONTHLY_INCREASE, increase);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
