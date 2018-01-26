package com.intelliviz.retirementhelper.ui.income;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.SavingsIncomeEditViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_TYPE;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

public class SavingsIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private static final int START_AGE = 0;
    private static final int STOP_AGE = 1;
    private SavingsIncomeEntity mSIE;
    private long mId;
    private int mIncomeType;
    private int mAgeType = START_AGE;
    private boolean mActivityResult;
    private SavingsIncomeEditViewModel mViewModel;

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

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

    @BindView(R.id.start_age_text_view)
    TextView mStartAgeTextView;

    @BindView(R.id.withdraw_percent_edit_text)
    TextView mInitWithdrawPercentTextView;

    @BindView(R.id.annual_percent_increase_edit_text)
    EditText mAnnualPercentIncrease;

    @BindView(R.id.input_withdraw_percent)
    android.support.design.widget.TextInputLayout mInputWithdrawPercent;

    @BindView(R.id.stop_age_text_view)
    TextView mStopMonthlyAdditionAgeTextView;

    @OnClick(R.id.edit_stop_age_button) void editStopMonthlyAdditionAge() {
        AgeData stopAge = mSIE.getStopMonthlyAdditionAge();
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+stopAge.getYear(), ""+stopAge.getMonth());
        dialog.show(fm, "");
        mAgeType = STOP_AGE;
    }

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
    }

    @OnClick(R.id.edit_start_age_button) void editStartAge() {
        AgeData startAge = mSIE.getStartAge();
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
        mAgeType = START_AGE;
    }

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
            mIncomeType = intent.getIntExtra(EXTRA_INCOME_TYPE, 0);
            int rc = intent.getIntExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, 0);
            mActivityResult = RetirementConstants.ACTIVITY_RESULT == rc;
        }

        mSIE = null;

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

        mInitWithdrawPercentTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mInitWithdrawPercentTextView.setText(interest);
                    } else {
                        mInitWithdrawPercentTextView.setText("");
                    }
                }
            }
        });

        mAnnualPercentIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualPercentIncrease.setText(interest);
                    } else {
                        mAnnualPercentIncrease.setText("");
                    }
                }
            }
        });

        SavingsIncomeEditViewModel.Factory factory = new
                SavingsIncomeEditViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(SavingsIncomeEditViewModel.class);

        mViewModel.getData().observe(this, new Observer<SavingsIncomeEntity>() {
            @Override
            public void onChanged(@Nullable SavingsIncomeEntity data) {
                mSIE = data;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (mSIE == null) {
            return;
        }

        mInitWithdrawPercentTextView.setText(mSIE.getWithdrawPercent()+"%");

        String incomeSourceName = mSIE.getName();
        int type = mSIE.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, mIncomeType);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);

        String balanceString;
        balanceString = mSIE.getBalance();
        balanceString = SystemUtils.getFormattedCurrency(balanceString);

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mSIE.getMonthlyAddition());
        AgeData age;


        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);

        String interest = mSIE.getInterest()+"%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(monthlyIncreaseString);

        age = mSIE.getStartAge();
        mStartAgeTextView.setText(age.toString());

        age = mSIE.getStopMonthlyAdditionAge();
        mStopMonthlyAdditionAgeTextView.setText(age.toString());

        String increase = mSIE.getAnnualPercentIncrease()+"%";
        mAnnualPercentIncrease.setText(increase);
    }

    private void updateIncomeSourceData() {
        String value = mBalance.getText().toString();
        String balance = getFloatValue(value);
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.balance_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualInterest.getText().toString();
        String interest = getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyAddition = getFloatValue(value);
        if(monthlyAddition == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String withdrawPercent = mInitWithdrawPercentTextView.getText().toString();

        value = withdrawPercent;
        withdrawPercent = getFloatValue(withdrawPercent);
        if(withdrawPercent == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_increase_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualPercentIncrease.getText().toString();
        String annualPercentIncrease = getFloatValue(value);
        if(annualPercentIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.annual_withdraw_increase_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String age = mStartAgeTextView.getText().toString();
        String age2 = SystemUtils.trimAge(age);
        AgeData startAge = SystemUtils.parseAgeString(age2);

        age = mStopMonthlyAdditionAgeTextView.getText().toString();
        age2 = SystemUtils.trimAge(age);
        AgeData stopAge = SystemUtils.parseAgeString(age2);

        String name = mIncomeSourceName.getText().toString();
        SavingsIncomeEntity sie = new SavingsIncomeEntity(mId, mIncomeType, name, startAge, balance, interest, monthlyAddition,
                stopAge, withdrawPercent, annualPercentIncrease);
        if(mActivityResult) {
            sendData(mId, name, startAge, balance, interest, monthlyAddition, stopAge, withdrawPercent, annualPercentIncrease);
        } else {
            mViewModel.setData(sie);
        }

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        if(mAgeType == START_AGE) {
            mStartAgeTextView.setText(age.toString());
        } else {
            mStopMonthlyAdditionAgeTextView.setText(age.toString());
        }
    }

    private void sendData(long id, String name, AgeData startAge, String balance, String interest, String monthlyAddition,
                          AgeData stopMonthlyAdditionAge, String withdrawPercent, String annualPercentIncrease) {
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, id);
        bundle.putInt(RetirementConstants.EXTRA_INCOME_TYPE, mIncomeType);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        bundle.putParcelable(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE, startAge);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, interest);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_INCREASE, monthlyAddition);
        bundle.putParcelable(RetirementConstants.EXTRA_INCOME_STOP_MONTHLY_ADDITION_AGE, stopMonthlyAdditionAge);
        bundle.putString(RetirementConstants.EXTRA_WITHDRAW_PERCENT, withdrawPercent);
        bundle.putString(RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE, annualPercentIncrease);
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
