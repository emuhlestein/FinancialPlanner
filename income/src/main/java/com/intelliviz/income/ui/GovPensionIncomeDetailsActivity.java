package com.intelliviz.income.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.income.R;
import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.IncomeDetails;
import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.util.RetirementConstants;
import com.intelliviz.income.util.SystemUtils;
import com.intelliviz.income.viewmodel.GovPensionIncomeDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class GovPensionIncomeDetailsActivity extends AppCompatActivity implements IncomeDetailsSelectListener{
    private GovPensionIncomeDetailsViewModel mViewModel;
    private GovPensionEntity mGPE;
    private long mId;

    private Toolbar mToolbar;
    private android.support.design.widget.AppBarLayout mAppBarLayout;
    private TextView mPrincipleSpouseTextView;
    private TextView mNameTextView;
    private TextView mStartAgeTextView;
    private TextView mStartAgeLabel;
    private LinearLayout mActualStartAgeLayout;
    private TextView mActualStartAgeTextView;
    private TextView mFullMonthlyBenefitTextView;
    private TextView mMonthlyBenefitTextView;
    private TextView mFullRetirementAgeTextView;
    private LinearLayout mExpandedTextLayout;
    private FloatingActionButton mEditPensionFAB;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gov_pension_income_details);

        mToolbar = findViewById(R.id.income_source_toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        mPrincipleSpouseTextView = findViewById(R.id.principle_spouse_text_view);
        mNameTextView = findViewById(R.id.name_text_view);
        mStartAgeTextView = findViewById(R.id.start_age_text_view);
        mStartAgeLabel = findViewById(R.id.start_age_label);
        mActualStartAgeLayout = findViewById(R.id.actual_start_age_layout);
        mActualStartAgeTextView = findViewById(R.id.actual_start_age_text_view);
        mFullMonthlyBenefitTextView = findViewById(R.id.full_monthly_benefit_text_view);
        mMonthlyBenefitTextView = findViewById(R.id.monthly_benefit_text_view);
        mFullRetirementAgeTextView = findViewById(R.id.full_retirement_age_text_view);
        mExpandedTextLayout = findViewById(R.id.expanded_text_layout);
        mEditPensionFAB = findViewById(R.id.editPensionFAB);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);

        mPrincipleSpouseTextView.setVisibility(GONE);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, 0);
        }

        /*
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    mExpandedTextLayout.setVisibility(View.GONE);
                    mCollapsingToolbarLayout.setTitle(getApplicationName(GovPensionIncomeDetailsActivity.this));
                } else {
                    isShow = false;
                    mExpandedTextLayout.setVisibility(View.VISIBLE);
                    mCollapsingToolbarLayout.setTitle("");
                }
            }
        });
        */

        List<IncomeDetails> incomeDetails = new ArrayList<>();
        //mAdapter = new IncomeDetailsAdapter(this, incomeDetails);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.setAdapter(mAdapter);

        //mAdapter.setIncomeDetailsSelectListener(this);

        // The FAB will pop up an activity to allow a new income source to be edited
        mEditPensionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GovPensionIncomeDetailsActivity.this, GovPensionIncomeEditActivity.class);
                intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mId);
                intent.putExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, RetirementConstants.ACTIVITY_RESULT);
                startActivity(intent);
            }
        });


        // For adding dividing line between views
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
        //        linearLayoutManager.getOrientation()));
        GovPensionIncomeDetailsViewModel.Factory factory = new
                GovPensionIncomeDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionIncomeDetailsViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<IncomeDetails>>() {
            @Override
            public void onChanged(@Nullable List<IncomeDetails> listIncomeDetails) {

                if(listIncomeDetails == null) {
                    return;
                }
                //mAdapter.update(listIncomeDetails);
            }
        });

        mViewModel.get().observe(this, new Observer<GovPensionEntity>() {
            @Override
            public void onChanged(@Nullable GovPensionEntity gpe) {
                mGPE = gpe;
                updateUI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    private void updateUI() {
        if(mGPE == null) {
            return;
        }

        SystemUtils.setToolbarSubtitle(this, "Social Security - " + mGPE.getName());

        if(mGPE.isPrincipleSpouse()) {
            mPrincipleSpouseTextView.setVisibility(View.VISIBLE);
        }

        mNameTextView.setText(mGPE.getName());

        AgeData age = mGPE.getStartAge();
        AgeData actualStartAge = mGPE.getActualStartAge();

        if(actualStartAge != null) {
            String temp = getResources().getString(R.string.desired_start_age);
            mStartAgeLabel.setText(temp);
            mStartAgeTextView.setText(age.toString());
            mActualStartAgeTextView.setText(actualStartAge.toString());
            mActualStartAgeLayout.setVisibility(View.VISIBLE);
        } else {
            mActualStartAgeLayout.setVisibility(View.GONE);
            mStartAgeTextView.setText(age.toString());
        }

        age = mGPE.getFullRetirementAge();
        mFullRetirementAgeTextView.setText(age.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mGPE.getFullMonthlyBenefit());
        mFullMonthlyBenefitTextView.setText(formattedValue);

        formattedValue = SystemUtils.getFormattedCurrency(mGPE.getMonthlyBenefit());
        mMonthlyBenefitTextView.setText(formattedValue);

        // TODO add multiline text view for info about benefits
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    @Override
    public void onIncomeDetailsSelect(IncomeDetails incomeDetails) {
        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, incomeDetails.getMessage(), Snackbar.LENGTH_INDEFINITE);
        View view = snackbar.getView();
        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(3);
        snackbar.setAction("DISMISS", new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
