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
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.ui.AgeDialog;
import com.intelliviz.retirementhelper.ui.BirthdateActivity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.GovPensionIncomeEditViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

public class GovPensionIncomeEditActivity extends AppCompatActivity implements AgeDialog.OnAgeEditListener {

    private GovPensionEntity mGPE;
    private long mId;
    private boolean mActivityResult;
    private boolean mIsSpousalBenefits;
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

    @BindView(R.id.spousal_benefit_text_view)
    TextView mSpousalBenefits;

    @BindView(R.id.spousal_benefit_label)
    TextView mSpousalBenefitsLabel;

    @BindView(R.id.spouse_birthdate_text_view)
    TextView mSpouseBirthdate;

    @OnClick(R.id.edit_spouse_birthdate_button) void editBirthdate() {
        // TODO clean this up, maybe need default entity
        String birthdate;
        if(mGPE != null) {
            birthdate = mGPE.getSpouseBirhtdate();
        } else {
            birthdate = "01-01-1900";
        }
        Intent newIntent = new Intent(this, BirthdateActivity.class);
        newIntent.putExtra(EXTRA_BIRTHDATE, birthdate);
        startActivityForResult(newIntent, REQUEST_BIRTHDATE);
    }

    @BindView(R.id.edit_spouse_birthdate_button)
    TextView mSpouseBirthdateButton;

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
        mActivityResult = false;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
            int rc = intent.getIntExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, 0);
            mActivityResult = RetirementConstants.ACTIVITY_RESULT == rc ? true : false;
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

        mViewModel = ViewModelProviders.of(this).
                get(GovPensionIncomeEditViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<GovPensionEntity>>() {
            @Override
            public void onChanged(@Nullable List<GovPensionEntity> govPensionIncomeList) {
                List<GovPensionEntity> list = govPensionIncomeList;
                if (list != null && list.size() > 1 && mId == 0) {
                    final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.only_two_gov_pension, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                            finish();
                        }
                    });
                    snackbar.show();
                } else {
                    mIsSpousalBenefits = false;
                    if(list.size() == 1) {
                        if(mId > 0) {
                            // non-spousal
                            mGPE = list.get(0);
                        } else if(mId == 0) {
                            // new spousal benefit
                            mIsSpousalBenefits = true;
                        }
                    } else if(list.size() == 2){
                        if(list.get(0).getId() == mId) {
                            mGPE = list.get(0);
                        } else {
                            mGPE = list.get(1);
                            mIsSpousalBenefits = true;
                        }
                    }

                    updateUI();

                    if(mIsSpousalBenefits) {
                        mSpousalBenefits.setEnabled(true);
                        mSpousalBenefitsLabel.setEnabled(true);
                        mSpouseBirthdate.setEnabled(true);
                        mSpouseBirthdateButton.setEnabled(true);
                    } else {
                        mSpousalBenefits.setEnabled(false);
                        mSpousalBenefitsLabel.setEnabled(false);
                        mSpouseBirthdate.setEnabled(false);
                        mSpouseBirthdateButton.setEnabled(false);
                    }
                }
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
        if(mGPE == null) {
            if(mIsSpousalBenefits) {
                mSpousalBenefits.setText("Yes");
            } else {
                mSpousalBenefits.setText("No");
            }
            return;
        }

        mName.setText(mGPE.getName());

        AgeData age = mGPE.getFullRetirementAge();
        mFullRetirementAge.setText(age.toString());

        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGPE.getFullMonthlyBenefit());
        mFullMonthlyBenefit.setText(monthlyBenefit);

        age = mGPE.getStartAge();
        mStartRetirementAge.setText(age.toString());

        mSpousalBenefits.setText("No");
        boolean includeSpouse = mGPE.getSpouse() == 1;
        if(includeSpouse) {
            mSpouseBirthdate.setText(mGPE.getSpouseBirhtdate());
            mSpousalBenefits.setText("Yes");
        }

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

        String spouseBirthdate = "0";

        if (mIsSpousalBenefits) {
            spouseBirthdate = mSpouseBirthdate.getText().toString();

            if (!SystemUtils.validateBirthday(spouseBirthdate)) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
        }

        String age = mStartRetirementAge.getText().toString();
        String age2 = SystemUtils.trimAge(age);
        AgeData startAge = SystemUtils.parseAgeString(age2);

        GovPensionEntity gpe = new GovPensionEntity(mId, INCOME_TYPE_GOV_PENSION, name,
                fullBenefit, startAge, mIsSpousalBenefits ? 1 : 0, spouseBirthdate);
        if(mActivityResult) {
            sendData(mId, name, fullBenefit, startAge,  mIsSpousalBenefits ? 1 : 0, spouseBirthdate);
        } else {
            mViewModel.setData(gpe);
        }

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = parseAgeString(year, month);
        mStartRetirementAge.setText(age.toString());
    }

    private void sendData(long id, String name, String fullMonthlyBenefit, AgeData startAge,
                          int includeSpouse, String spouseBirhtdate) {
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, id);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        bundle.putParcelable(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE, startAge);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_BENEFIT, fullMonthlyBenefit);
        bundle.putInt(RetirementConstants.EXTRA_INCOME_SOURCE_INCLUDE_SPOUSE, includeSpouse);
        bundle.putString(RetirementConstants.EXTRA_INCOME_SOURCE_SPOUSE_BIRTHDAY, spouseBirhtdate);

        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
    }
}
