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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.util.AgeUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.PensionIncomeEditViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

public class PensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {
    private static final String TAG = PensionIncomeEditActivity.class.getSimpleName();
    private PensionIncomeEntity mPIE;
    private long mId;
    private boolean mActivityResult;
    private PensionIncomeEditViewModel mViewModel;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @BindView(R.id.minimum_age_text)
    TextView mMinAge;

    @OnClick(R.id.edit_minimum_age_button) void editAge() {
        AgeData startAge = mPIE.getMinAge();
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
        //finish();
        //overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
            int rc = intent.getIntExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, 0);
            mActivityResult = RetirementConstants.ACTIVITY_RESULT == rc;
        }

        mPIE = null;

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

        PensionIncomeEditViewModel.Factory factory = new
                PensionIncomeEditViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(PensionIncomeEditViewModel.class);

        mViewModel.getData().observe(this, new Observer<PensionIncomeEntity>() {
            @Override
            public void onChanged(@Nullable PensionIncomeEntity data) {
                mPIE = data;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if(mPIE == null) {
            return;
        }
        String name = mPIE.getName();
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPIE.getMonthlyBenefit());
        AgeData minAge = mPIE.getMinAge();

        mIncomeSourceName.setText(name);
        mMinAge.setText(minAge.toString());
        mMonthlyBenefit.setText(monthlyBenefit);

        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, INCOME_TYPE_PENSION);
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

        age = AgeUtils.trimAge(age);
        AgeData minAge = AgeUtils.parseAgeString(age);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if(mActivityResult) {
            sendData(mId, name, benefit, minAge);
        } else {
            PensionIncomeEntity pie = new PensionIncomeEntity(mId, INCOME_TYPE_PENSION, name, minAge, benefit);
            mViewModel.setData(pie);
        }

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = AgeUtils.parseAgeString(year, month);
        mMinAge.setText(age.toString());
    }

    private void sendData(long id, String name, String monthlyBenefit, AgeData minAge) {
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, id);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        bundle.putParcelable(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE, minAge);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_BENEFIT, monthlyBenefit);
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
