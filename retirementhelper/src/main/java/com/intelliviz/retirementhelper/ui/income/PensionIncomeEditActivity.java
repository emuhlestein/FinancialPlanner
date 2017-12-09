package com.intelliviz.retirementhelper.ui.income;

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
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.PensionIncomeViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

public class PensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private static final String TAG = PensionIncomeEditActivity.class.getSimpleName();
    private PensionIncomeEntity mPID;
    private long mId;
    private PensionIncomeViewModel mViewModel;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @BindView(R.id.minimum_age_text)
    TextView mMinAge;

    @OnClick(R.id.edit_minimum_age_button) void editAge() {
        String age = mPID.getMinAge();
        AgeData startAge = SystemUtils.parseAgeString(age);
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

    @BindView(R.id.monthly_benefit_text)
    EditText mMonthlyBenefit;

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pension_income);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mPID = null;

        mMonthlyBenefit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
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

        mViewModel.getData().observe(this, new Observer<PensionIncomeEntity>() {
            @Override
            public void onChanged(@Nullable PensionIncomeEntity data) {
                mPID = data;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if(mPID == null) {
            return;
        }
        String name = mPID.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPID.getMonthlyBenefit());
        String age = mPID.getMinAge();

        mIncomeSourceName.setText(name);

        AgeData minAge = SystemUtils.parseAgeString(age);
        mMinAge.setText(minAge.toString());
        mMonthlyBenefit.setText(monthlyBenefit);

        int type = mPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);
    }

    public void updateIncomeSourceData() {
        String name = mIncomeSourceName.getText().toString();
        String age = mMinAge.getText().toString();
        String value = mMonthlyBenefit.getText().toString();
        String benefit = getFloatValue(value);
        if(benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        age = SystemUtils.trimAge(age);
        AgeData minAge = SystemUtils.parseAgeString(age);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        PensionIncomeEntity pid = new PensionIncomeEntity(mId, INCOME_TYPE_PENSION, name, age, benefit);
        mViewModel.setData(pid);
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mMinAge.setText(age.toString());
    }
}
