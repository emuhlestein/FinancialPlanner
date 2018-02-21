package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.ui.BirthdateActivity;
import com.intelliviz.retirementhelper.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.GovPensionIncomeEditViewModel;
import com.intelliviz.retirementhelper.viewmodel.LiveDataWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;
import static com.intelliviz.retirementhelper.viewmodel.GovPensionIncomeEditViewModel.ERROR_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.viewmodel.GovPensionIncomeEditViewModel.ERROR_ONLY_TWO_SOCIAL_SECURITY;

public class GovPensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {

    private GovPensionEntity mGPE;
    private long mId;
    private GovPensionIncomeEditViewModel mViewModel;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView( R.id.name_edit_text)
    EditText mName;

    @BindView(R.id.full_retirement_age_text_view)
    TextView mFullRetirementAge;

    @BindView(R.id.full_monthly_benefit_edit_text)
    EditText mFullMonthlyBenefit;

    @BindView(R.id.start_age_text_view)
    TextView mStartRetirementAge;

    @OnClick(R.id.edit_start_age_button) void editAge() {

        AgeData startAge;
        // TODO clean this up, maybe need default entity
        if(mGPE != null) {
            startAge = mGPE.getStartAge();
        } else {
            startAge = new AgeData(0, 0);
        }
        FragmentManager fm = getSupportFragmentManager();
        AgeDialog dialog = AgeDialog.newInstance(""+startAge.getYear(), ""+startAge.getMonth());
        dialog.show(fm, "");
    }

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gov_pension_income);

        ButterKnife.bind(this);

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
                if(gpe.getState() == ERROR_ONLY_TWO_SOCIAL_SECURITY) {
                    final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, gpe.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                            finish();
                        }
                    });
                    snackbar.show();
                } else if(gpe.getState() == ERROR_NO_SPOUSE_BIRTHDATE) {
                    showDialog("01-01-1900", new BirthdateDialogAction() {
                        @Override
                        public void onGetBirthdate(String birthdate) {
                            mViewModel.updateSpouseBirthdate(birthdate);
                        }
                    });
                    //Intent newIntent = new Intent(GovPensionIncomeEditActivity.this, PersonalInfoDialog.class);
                    //startActivity(newIntent);
                    /*
                    final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, gpe.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newIntent = new Intent(GovPensionIncomeEditActivity.this, BirthdateActivity.class);
                            startActivityForResult(newIntent, REQUEST_SPOUSE_BIRTHDATE);
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    */
                }
                mGPE = (GovPensionEntity) gpe.getObj();
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

        mName.setText(mGPE.getName());

        AgeData age = mGPE.getFullRetirementAge();
        mFullRetirementAge.setText(age.toString());

        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPE.getFullMonthlyBenefit());
        mFullMonthlyBenefit.setText(monthlyBenefit);

        age = mGPE.getStartAge();
        mStartRetirementAge.setText(age.toString());

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

        String age = mStartRetirementAge.getText().toString();
        String age2 = SystemUtils.trimAge(age);
        AgeData startAge = SystemUtils.parseAgeString(age2);

        GovPensionEntity gpe = new GovPensionEntity(mGPE.getId(), mGPE.getType(), name,
                fullBenefit, startAge, mGPE.getSpouse());
        mViewModel.setData(gpe);

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mStartRetirementAge.setText(age.toString());
    }

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BirthdateActivity birthdateDialog = BirthdateActivity.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }
}
