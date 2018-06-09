package com.intelliviz.retirementhelper.ui.income;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.intelliviz.retirementhelper.ui.SavingsAdvancedFragment;
import com.intelliviz.retirementhelper.util.AgeUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.SavingsIncomeEditViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_TYPE;

public class SavingsIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private static final int START_AGE = 0;
    private static final int STOP_AGE = 1;
    private SavingsIncomeEntity mSIE;
    private long mId;
    private int mIncomeType;
    private int mAgeType = STOP_AGE;
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

    @BindView(R.id.start_age_text_view)
    TextView mStartAgeTextView;

    @BindView(R.id.withdraw_percent_edit_text)
    TextView mInitWithdrawPercentTextView;

    @OnClick(R.id.edit_start_age_button) void editStartAge() {
        AgeData startAge = mSIE.getStartAge();
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
        mAgeType = START_AGE;
    }

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
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
                    String value = SystemUtils.getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
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
                    interest = SystemUtils.getFloatValue(interest);
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
                    interest = SystemUtils.getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mInitWithdrawPercentTextView.setText(interest);
                    } else {
                        mInitWithdrawPercentTextView.setText("");
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

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);

        String interest = mSIE.getInterest()+"%";
        mAnnualInterest.setText(interest);

        String monthlyAdditionString = SystemUtils.getFormattedCurrency(mSIE.getMonthlyAddition());
        setMonthlyAddition(monthlyAdditionString);

        AgeData age;
        age = mSIE.getStartAge();
        mStartAgeTextView.setText(age.toString());

        age = mSIE.getStopMonthlyAdditionAge();
        if(age != null) {
            setStopMonthlyAdditionAge(age.toString());
        }

        String increase = mSIE.getAnnualPercentIncrease()+"%";
        setAnnualPercentIncrease(increase);

        setShowMonths(mSIE.getShowMonths() == 1);
    }

    private void updateIncomeSourceData() {
        String value = mBalance.getText().toString();
        String balance = SystemUtils.getFloatValue(value);
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.balance_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualInterest.getText().toString();
        String interest = SystemUtils.getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = getMonthlyAddition();
        String monthlyAddition = SystemUtils.getFloatValue(value);
        if(monthlyAddition == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String withdrawPercent = mInitWithdrawPercentTextView.getText().toString();

        value = withdrawPercent;
        withdrawPercent = SystemUtils.getFloatValue(withdrawPercent);
        if(withdrawPercent == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_increase_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = getAnnualPercentIncrease();
        String annualPercentIncrease = SystemUtils.getFloatValue(value);
        if(annualPercentIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.annual_withdraw_increase_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String age = mStartAgeTextView.getText().toString();
        String age2 = AgeUtils.trimAge(age);
        AgeData startAge = AgeUtils.parseAgeString(age2);

        age = getStopMonthlyAdditionAge();
        age2 = AgeUtils.trimAge(age);
        AgeData stopAge = AgeUtils.parseAgeString(age2);

        String name = mIncomeSourceName.getText().toString();

        int showMonths = getShowMonths() ? 1 : 0;
        SavingsIncomeEntity sie = new SavingsIncomeEntity(mId, mIncomeType, name, startAge, balance, interest, monthlyAddition,
                stopAge, withdrawPercent, annualPercentIncrease, showMonths);
        if(mActivityResult) {
            sendData(mId, name, startAge, balance, interest, monthlyAddition, stopAge,
                    withdrawPercent, annualPercentIncrease, showMonths);
        } else {
            mViewModel.setData(sie);
        }

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = AgeUtils.parseAgeString(year, month);
        if(mAgeType == START_AGE) {
            mStartAgeTextView.setText(age.toString());
        }
    }

    private void sendData(long id, String name, AgeData startAge, String balance, String interest, String monthlyAddition,
                          AgeData stopMonthlyAdditionAge, String withdrawPercent, String annualPercentIncrease,
                          int showMonths) {
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
        bundle.putString(RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT, withdrawPercent);
        bundle.putString(RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE, annualPercentIncrease);
        bundle.putInt(RetirementConstants.EXTRA_INCOME_SHOW_MONTHS, showMonths);
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private String getStopMonthlyAdditionAge() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            return ((SavingsAdvancedFragment)fragment).getStopMonthlyAdditionAge();
        }
        return "0";
    }

    private void setStopMonthlyAdditionAge(String stopMonthlyAdditionAge) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            ((SavingsAdvancedFragment)fragment).setStopMonthlyAdditionAge(stopMonthlyAdditionAge);
        }
    }

    private String getAnnualPercentIncrease() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            return ((SavingsAdvancedFragment)fragment).getAnnualPercentIncrease();
        }
        return "0";
    }

    private void setAnnualPercentIncrease(String annualPercentIncrease) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            ((SavingsAdvancedFragment)fragment).setAnnualPercentIncrease(annualPercentIncrease);
        }
    }

    private String getMonthlyAddition() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            return ((SavingsAdvancedFragment)fragment).getMonthlyAddition();
        }
        return "0";
    }

    private void setMonthlyAddition(String monthlyIncrease) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            ((SavingsAdvancedFragment)fragment).setMonthlyAddition(monthlyIncrease);
        }
    }

    private void setShowMonths(boolean showMonths) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            ((SavingsAdvancedFragment)fragment).setShowMonths(showMonths);
        }
    }

    private boolean getShowMonths() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.savings_advanced_fragment);
        if(fragment != null && fragment instanceof SavingsAdvancedFragment) {
            return ((SavingsAdvancedFragment)fragment).getShowMonths();
        }
        return false;
    }
}
