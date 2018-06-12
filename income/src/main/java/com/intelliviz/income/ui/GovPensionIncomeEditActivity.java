package com.intelliviz.income.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.income.R;
import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.util.AgeUtils;
import com.intelliviz.income.util.BirthdateDialogAction;
import com.intelliviz.income.util.RetirementConstants;
import com.intelliviz.income.util.SystemUtils;
import com.intelliviz.income.viewmodel.GovPensionIncomeEditViewModel;
import com.intelliviz.income.viewmodel.LiveDataWrapper;

import static com.intelliviz.income.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY;
import static com.intelliviz.income.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY_FREE;
import static com.intelliviz.income.util.RetirementConstants.EC_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.income.util.RetirementConstants.EC_PRINCIPLE_SPOUSE;
import static com.intelliviz.income.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.income.util.RetirementConstants.REQUEST_SPOUSE_BIRTHDATE;
import static com.intelliviz.income.util.SystemUtils.getFloatValue;


public class GovPensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {

    private GovPensionEntity mGPE;
    private long mId;
    private GovPensionIncomeEditViewModel mViewModel;
    private boolean mIsPrincipleSpouse;

    private CoordinatorLayout mCoordinatorLayout;
    private EditText mName;
    private TextView mPrincipleSpouseLabel;
    private TextView mFullRetirementAge;
    private EditText mFullMonthlyBenefit;
    private Toolbar mToolbar;
    private Button mAddIncomeSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gov_pension_income);

        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mName = findViewById(R.id.name_edit_text);
        mPrincipleSpouseLabel = findViewById(R.id.principle_spouse_label);
        mFullRetirementAge = findViewById(R.id.full_retirement_age_text_view);
        mFullMonthlyBenefit = findViewById(R.id.full_monthly_benefit_edit_text);
        mToolbar = findViewById(R.id.income_source_toolbar);
        mFullRetirementAge = findViewById(R.id.full_retirement_age_text_view);
        mAddIncomeSource = findViewById(R.id.add_income_source_button);
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
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
        }

        mFullMonthlyBenefit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    String formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mFullMonthlyBenefit.setText(formattedString);
                    }
                }
            }
        });

        GovPensionIncomeEditViewModel.Factory factory = new
                GovPensionIncomeEditViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionIncomeEditViewModel.class);

        mViewModel.get().observe(this, new Observer<LiveDataWrapper>() {
            @Override
            public void onChanged(@Nullable LiveDataWrapper gpe) {
                mGPE = (GovPensionEntity) gpe.getObj();
                mIsPrincipleSpouse = false;
                switch(gpe.getState()) {
                    case EC_MAX_NUM_SOCIAL_SECURITY:
                        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, gpe.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                finish();
                            }
                        });
                        snackbar.show();
                        break;
                    case EC_MAX_NUM_SOCIAL_SECURITY_FREE:
                        final Snackbar snackbar1 = Snackbar.make(mCoordinatorLayout, gpe.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar1.setAction(R.string.dismiss, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar1.dismiss();
                                finish();
                            }
                        });
                        snackbar1.show();
                        break;
                    case EC_NO_SPOUSE_BIRTHDATE:
                        showDialog("01-01-1900", new BirthdateDialogAction() {
                            @Override
                            public void onGetBirthdate(String birthdate) {
                                mViewModel.updateSpouseBirthdate(birthdate);
                            }
                        });
                        break;
                    case EC_PRINCIPLE_SPOUSE:
                        mIsPrincipleSpouse = true;
                        break;
                }
                updateUI();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SPOUSE_BIRTHDATE:
                    String birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    mViewModel.updateSpouseBirthdate(birthdate);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

    private void updateUI() {
        if(mGPE == null) {
            return;
        }

        if(mIsPrincipleSpouse) {
            mPrincipleSpouseLabel.setVisibility(View.VISIBLE);
        } else {
            mPrincipleSpouseLabel.setVisibility(View.GONE);
        }

        mName.setText(mGPE.getName());

        AgeData age = mGPE.getFullRetirementAge();
        mFullRetirementAge.setText(age.toString());

        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPE.getFullMonthlyBenefit());
        mFullMonthlyBenefit.setText(monthlyBenefit);

        age = mGPE.getStartAge();
        setStartRetirementAge(age.toString());

        int type = mGPE.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);
    }

    private void updateIncomeSourceData() {
        String name = mName.getText().toString();
        String value = mFullMonthlyBenefit.getText().toString();
        String fullBenefit = getFloatValue(value);
        if (fullBenefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_benefit_not_valid) + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        AgeData startAge = getStartRetirementAge();

        GovPensionEntity gpe = new GovPensionEntity(mGPE.getId(), mGPE.getType(), name,
                fullBenefit, startAge, mGPE.getSpouse());
        mViewModel.setData(gpe);

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = AgeUtils.parseAgeString(year, month);
        setStartRetirementAge(age.toString());
    }

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BirthdateActivity birthdateDialog = BirthdateActivity.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birthdate");
    }

    private void setStartRetirementAge(String age) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.gov_pension_advanced_fragment);
        if(fragment != null && fragment instanceof GovPensionAdvancedFragment) {
            ((GovPensionAdvancedFragment)fragment).setStartRetirementAge(age.toString());
        }
    }

    private AgeData getStartRetirementAge() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.gov_pension_advanced_fragment);
        if(fragment != null && fragment instanceof GovPensionAdvancedFragment) {
            String age = ((GovPensionAdvancedFragment)fragment).getStartRetirementAge();
            String trimmedAge = AgeUtils.trimAge(age);
            AgeData startAge = AgeUtils.parseAgeString(trimmedAge);
            if(startAge != null) {
                return startAge;
            }
        }
        return new AgeData(0);
    }
}
