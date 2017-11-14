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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.ui.BirthdateActivity;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.GovPensionViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

public class GovPensionIncomeActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {

    private GovPensionEntity mGPID;
    private long mId;
    private GovPensionViewModel mViewModel;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.monthly_benefit_text)
    EditText mMonthlyBenefit;

    @BindView(R.id.start_age_text_view)
    TextView mStartAge;

    @OnClick(R.id.edit_start_age_button) void editAge() {
        String age = mGPID.getStartAge();
        AgeData startAge = SystemUtils.parseAgeString(age);
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

    @BindView(R.id.spouse_check_box)
    CheckBox mIncludeSpouse;

    @BindView(R.id.spouse_monthly_benefit_text)
    EditText mSpouseMonthlyBenefit;

    @BindView(R.id.spouse_birthdate_text_view)
    TextView mSpouseBirthdate;

    @OnClick(R.id.edit_birthdate_button) void editBirthdate() {
        Intent newIntent = new Intent(this, BirthdateActivity.class);
        startActivityForResult(newIntent, REQUEST_BIRTHDATE);
    }

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gov_pension_income);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mIncludeSpouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSpouseMonthlyBenefit.setEnabled(true);
                    mSpouseBirthdate.setEnabled(true);
                } else {
                    mSpouseMonthlyBenefit.setEnabled(false);
                    mSpouseBirthdate.setEnabled(false);
                }
            }
        });

        mSpouseMonthlyBenefit.setEnabled(false);
        mSpouseBirthdate.setEnabled(false);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

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

        GovPensionViewModel.Factory factory = new
                GovPensionViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionViewModel.class);

        mViewModel.getData().observe(this, new Observer<GovPensionEntity>() {
            @Override
            public void onChanged(@Nullable GovPensionEntity govPensionIncomeData) {
                mGPID = govPensionIncomeData;
                updateUI();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && requestCode == REQUEST_BIRTHDATE) {
            String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
            mSpouseBirthdate.setText(birthdate);
        }
    }

    private void updateUI() {
        if(mGPID == null || mGPID.getId() == 0) {
            return;
        }
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPID.getFullMonthlyBenefit());
        mMonthlyBenefit.setText(monthlyBenefit);

        AgeData age = SystemUtils.parseAgeString(mGPID.getStartAge());
        mStartAge.setText(age.toString());

        boolean includeSpouse = mGPID.getSpouse() == 1;
        if(includeSpouse) {
            String spouseBenefit = SystemUtils.getFormattedCurrency(mGPID.getSpouseBenefit());
            mSpouseMonthlyBenefit.setText(spouseBenefit);
            mSpouseBirthdate.setText(mGPID.getSpouseBirhtdate());
        }
        mIncludeSpouse.setChecked(includeSpouse);

        int type = mGPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);
    }

    private void updateIncomeSourceData() {
        String value = mMonthlyBenefit.getText().toString();
        String benefit = getFloatValue(value);
        if (benefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_benefit_not_valid) + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        int includeSpouse = mIncludeSpouse.isChecked() ? 1 : 0;
        String spouseBenefit = "0";
        String spouseBirthdate = "0";

        if (includeSpouse == 1) {
            value = mSpouseMonthlyBenefit.getText().toString();
            spouseBenefit = getFloatValue(value);
            if (benefit == null) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_benefit_not_valid) + value, Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }

            spouseBirthdate = mSpouseBirthdate.getText().toString();

            if (!SystemUtils.validateBirthday(spouseBirthdate)) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
        }

        String age = mStartAge.getText().toString();
        String age2 = SystemUtils.trimAge(age);

        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, INCOME_TYPE_GOV_PENSION);
        GovPensionEntity gpid = new GovPensionEntity(mId, INCOME_TYPE_GOV_PENSION, incomeSourceTypeString ,
                benefit, includeSpouse, spouseBenefit, spouseBirthdate, age2);
        mViewModel.setData(gpid);
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mStartAge.setText(age.toString());
    }
}
