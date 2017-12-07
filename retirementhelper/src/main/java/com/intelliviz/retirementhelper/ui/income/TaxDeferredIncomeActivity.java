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
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

public class TaxDeferredIncomeActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private TaxDeferredIncomeEntity mTDID;
    private long mId;
    private boolean mActivityResult;
    private TaxDeferredViewModel mViewModel;

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
    TextView mStartAge;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        finish();
    }

    @OnClick(R.id.edit_start_age_button) void editStartAge() {
        String age = mTDID.getStartAge();
        AgeData startAge = SystemUtils.parseAgeString(age);
        if(startAge == null) {
            startAge = new AgeData(59, 6);
        }
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tax_deferred_income);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
            int rc = intent.getIntExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, 0);
            mActivityResult = RetirementConstants.ACTIVITY_RESULT == rc;
        }

        mTDID = null;

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

        TaxDeferredViewModel.Factory factory = new
                TaxDeferredViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredViewModel.class);

        mViewModel.getData().observe(this, new Observer<TaxDeferredIncomeEntity>() {
            @Override
            public void onChanged(@Nullable TaxDeferredIncomeEntity data) {
                mTDID = data;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (mTDID == null || mTDID.getId() == 0) {
            return;
        }

        String incomeSourceName = mTDID.getName();
        int type = mTDID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);

        String balanceString;
        balanceString = mTDID.getBalance();
        balanceString = SystemUtils.getFormattedCurrency(balanceString);

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mTDID.getMonthlyIncrease());
        AgeData age;


        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);

        String interest = mTDID.getInterest()+"%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(monthlyIncreaseString);

        age = SystemUtils.parseAgeString(mTDID.getStartAge());
        if(age == null) {
            age = new AgeData(59, 6);
        }
        mStartAge.setText(age.toString());
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
        String monthlyIncrease = getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        String age = mStartAge.getText().toString();
        String age2 = SystemUtils.trimAge(age);

        String name = mIncomeSourceName.getText().toString();
        TaxDeferredIncomeEntity tdid = new TaxDeferredIncomeEntity(mId, INCOME_TYPE_TAX_DEFERRED, name, interest, monthlyIncrease, "10", "59 6", 1, balance, age2);
        if(mActivityResult) {
            sendData(mId, name, interest, monthlyIncrease, "10", "59 6", 1, balance, age2);
        } else {
            mViewModel.setData(tdid);
        }
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mStartAge.setText(age.toString());
    }

    private void sendData(long id, String name, String interest, String monthlyIncrease, String penalty, String minAge, int is401k, String balance, String startAge) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, id);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE, startAge);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, interest);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INCREASE, monthlyIncrease);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
