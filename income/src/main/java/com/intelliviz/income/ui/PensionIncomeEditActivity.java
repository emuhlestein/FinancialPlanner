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
import com.intelliviz.lowlevel.ui.MessageDialog;
import com.intelliviz.lowlevel.ui.NewMessageDialog;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import static com.intelliviz.income.util.uiUtils.getIncomeSourceTypeString;
import static com.intelliviz.lowlevel.util.RetirementConstants.EC_FOR_SELF_OR_SPOUSE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EC_ONLY_ONE_SUPPORTED;
import static com.intelliviz.lowlevel.util.RetirementConstants.EC_SPOUSE_INCLUDED;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;


public class PensionIncomeEditActivity extends AppCompatActivity implements
        AgeDialog.OnAgeEditListener, NewMessageDialog.DialogResponse {
    private static final String TAG = PensionIncomeEditActivity.class.getSimpleName();
    private PensionData mPD;
    private long mId;
    private PensionIncomeViewModel mViewModel;
    private boolean mSpouseIncluded;

    private CoordinatorLayout mCoordinatorLayout;
    private EditText mIncomeSourceName;
    private TextView mMinAge;
    private Button mEditMinimumgeButton;
    private Button mAddIncomeSourceButton;
    private EditText mMonthlyBenefit;
    private Toolbar mToolbar;
    private TextView mOwnerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pension_income);

        mOwnerTextView = findViewById(R.id.owner_text);

        mToolbar = findViewById(R.id.income_source_toolbar);
        mMonthlyBenefit = findViewById(R.id.monthly_benefit_text);
        mMinAge = findViewById(R.id.minimum_age_text);
        mIncomeSourceName = findViewById(R.id.name_edit_text);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mEditMinimumgeButton = findViewById(R.id.edit_minimum_age_button);
        mEditMinimumgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeData startAge = mPD.getStartAge();
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
            public void onChanged(@Nullable PensionViewData viewData) {
                FragmentManager fm ;

                if(viewData == null) {
                    return;
                }
                mSpouseIncluded = viewData.isSpouseIncluded();
                mPD = viewData.getPensionData();
                switch(viewData.getStatus()) {
                    case EC_ONLY_ONE_SUPPORTED:
                        fm = getSupportFragmentManager();
                        MessageDialog dialog = MessageDialog.newInstance("Warning", viewData.getMessage(), EC_ONLY_ONE_SUPPORTED, true, null, null);
                        dialog.show(fm, "message");
                        break;
                    case EC_SPOUSE_INCLUDED:
                        fm = getSupportFragmentManager();
                        NewMessageDialog newdialog = NewMessageDialog.newInstance(EC_FOR_SELF_OR_SPOUSE, "Income Source", "Is this income source for spouse or self?", "Self", "Spouse");
                        newdialog.show(fm, "message");
                        break;
                }
                updateUI();
            }
        });
    }

    private void updateUI() {
        if(mPD == null) {
            return;
        }
        String name = mPD.getName();
        if(!mSpouseIncluded) {
            mOwnerTextView.setVisibility(View.GONE);
        } else if(mPD.getOwner() == OWNER_PRIMARY) {
            mOwnerTextView.setText("Self");
        } else if(mPD.getOwner() == OWNER_SPOUSE) {
            mOwnerTextView.setText("Spouse");
        }
        String monthlyBenefit = SystemUtils.getFormattedCurrency(mPD.getMonthlyBenefit());
        AgeData minAge = mPD.getStartAge();

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

        PensionData pd = new PensionData(mId, INCOME_TYPE_PENSION, name, mPD.getOwner(), minAge, benefit);
        mViewModel.setData(pd);

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        // TODO check to see if age is valid
        AgeData age = new AgeData(year, month);
        mMinAge.setText(age.toString());
    }

    @Override
    public void onGetResponse(int id, int button) {
            switch (id) {
                case EC_ONLY_ONE_SUPPORTED:
                    finish();
                    break;
                case EC_FOR_SELF_OR_SPOUSE:
                    if (button == NewMessageDialog.POS_BUTTON) {
                        mPD.setOwner(RetirementConstants.OWNER_PRIMARY);
                    } else if(button == NewMessageDialog.NEG_BUTTON) {
                        mPD.setOwner(RetirementConstants.OWNER_SPOUSE);
                    }
                    updateUI();
                    break;
            }
    }
}
