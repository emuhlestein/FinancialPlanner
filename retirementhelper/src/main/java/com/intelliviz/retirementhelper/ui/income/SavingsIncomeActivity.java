package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.SavingsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

public class SavingsIncomeActivity extends AppCompatActivity {
    private long mId;
    private SavingsViewModel mViewModel;
    private SavingsIncomeEntity mSID;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @BindView(R.id.balance_text)
    EditText mBalance;

    @BindView(R.id.annual_interest_text)
    EditText mAnnualInterest;

    @BindView(R.id.monthly_increase_text)
    EditText mMonthlyIncrease;

    @BindView(R.id.add_income_source_button)
    Button mAddIncomeSource;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        finish();
    }

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_savings_income);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
                    }
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyIncrease.setText(formattedString);
                    }
                }
            }
        });

        mAnnualInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualInterest.setText(interest);
                    } else {
                        mAnnualInterest.setText("");
                    }
                }
            }
        });

        SavingsViewModel.Factory factory = new
                SavingsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(SavingsViewModel.class);

        mViewModel.getData().observe(this, new Observer<SavingsIncomeEntity>() {
            @Override
            public void onChanged(@Nullable SavingsIncomeEntity sid) {
                mSID = sid;
                updateUI();
            }
        });

    }
    private void updateUI() {
        if(mSID == null || mSID.getId() == 0) {
            return;
        }

        String incomeSourceName = mSID.getName();
        int type = mSID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);

        String balanceString = SystemUtils.getFormattedCurrency(mSID.getBalance());
        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease());
        String interestString = String.valueOf(mSID.getInterest());

        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(interestString);
        mMonthlyIncrease.setText(monthlyIncreaseString);
    }

    public void updateIncomeSourceData() {
        String balance = SystemUtils.getFloatValue(mBalance.getText().toString());
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,getString(R.string.balance_not_valid), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String value = mAnnualInterest.getText().toString();
        String interest = SystemUtils.getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,  getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyIncrease = SystemUtils.getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String name = mIncomeSourceName.getText().toString();
        double dbalance = Double.parseDouble(balance);
        double dinterest = Double.parseDouble(interest);
        double dmonthlyIncrease = Double.parseDouble(monthlyIncrease);

        SavingsIncomeEntity sid = new SavingsIncomeEntity(mId, INCOME_TYPE_SAVINGS, name, interest, monthlyIncrease, balance);
        mViewModel.setData(sid);
    }

}
