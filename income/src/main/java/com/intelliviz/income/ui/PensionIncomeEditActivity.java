package com.intelliviz.income.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.data.PensionData;
import com.intelliviz.income.R;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.income.viewmodel.PensionIncomeViewModel;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import static com.intelliviz.income.util.uiUtils.getIncomeSourceTypeString;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_PENSION;


public class PensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private static final String TAG = PensionIncomeEditActivity.class.getSimpleName();
    private PensionData mPD;
    private long mId;
    private boolean mActivityResult;
    private PensionIncomeViewModel mViewModel;

    private CoordinatorLayout mCoordinatorLayout;
    private EditText mIncomeSourceName;
    private TextView mMinAge;
    private Button mEditMinimumgeButton;
    private Button mAddIncomeSourceButton;
    private EditText mMonthlyBenefit;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pension_income);

        mToolbar = findViewById(R.id.income_source_toolbar);
        mMonthlyBenefit = findViewById(R.id.monthly_benefit_text);
        mMinAge = findViewById(R.id.minimum_age_text);
        mIncomeSourceName = findViewById(R.id.name_edit_text);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mEditMinimumgeButton = findViewById(R.id.edit_minimum_age_button);
        mEditMinimumgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeData startAge = mPD.getAge();
                FragmentManager fm = getSupportFragmentManager();
                AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
                dialog.show(fm, "");
            }
        });
        mAddIncomeSourceButton = findViewById(R.id.add_income_source_button);
        mAddIncomeSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIncomeSourceData();
            }
        });
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
            int rc = intent.getIntExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, 0);
            mActivityResult = RetirementConstants.ACTIVITY_RESULT == rc;
        }

        mPD = null;

        mMonthlyBenefit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String str = textView.getText().toString();
                    String value = SystemUtils.getFloatValue(str);
                    String formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyBenefit.setText(formattedString);
                    }
                }
            }
        });

        PensionIncomeViewModel.Factory factory = new
                PensionIncomeViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(PensionIncomeViewModel.class);

        mViewModel.get().observe(this, new Observer<PensionViewData>() {
            @Override
            public void onChanged(@Nullable PensionViewData data) {
                mPD = data.getPensionData();
                updateUI();
            }
        });
    }

    private void updateUI() {
        if(mPD == null) {
            return;
        }
        String name = mPD.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPD.getBenefit());
        AgeData minAge = mPD.getAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(minAge.toString());
        mMonthlyBenefit.setText(monthlyBenefit);

        String incomeSourceTypeString = getIncomeSourceTypeString(this, INCOME_TYPE_PENSION);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);
    }

    public void updateIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String value = mMonthlyBenefit.getText().toString();
        String benefit = SystemUtils.getFloatValue(value);
        if(benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        AgeData minAge = new AgeData(age);
        if(!minAge.isValid()) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        PensionData pd = new PensionData(mId, INCOME_TYPE_PENSION, name, minAge, benefit, 0);
        mViewModel.setData(pd);

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        // TODO check to see if age is valid
        AgeData age = new AgeData(year, month);
        mMinAge.setText(age.toString());
    }
}
