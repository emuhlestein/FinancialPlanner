package com.intelliviz.income.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.SavingsData;
import com.intelliviz.income.R;
import com.intelliviz.income.adapter.IncomeDetailsAdapter;
import com.intelliviz.income.viewmodel.SavingsIncomeViewModel;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class SavingsIncomeDetailsActivity extends AppCompatActivity {

    private IncomeDetailsAdapter mAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private SavingsIncomeViewModel mViewModel;
    private SavingsData mSIE;
    private long mId;

    private Toolbar mToolbar;
    private android.support.design.widget.AppBarLayout mAppBarLayout;
    private TextView mNameTextView;
    private TextView mStartAgeTextView;
    private TextView mAnnualInterestTextView;
    private TextView mMonthlyIncreaseTextView;
    private TextView mBalanceTextView;
    private LinearLayout mExpandedTextLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mEditSavingsFAB;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_income_details);

        mToolbar = findViewById(R.id.income_source_toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        mNameTextView = findViewById(R.id.name_text_view);
        mStartAgeTextView = findViewById(R.id.start_age_text_view);
        mAnnualInterestTextView = findViewById(R.id.annual_interest_text_view);
        mMonthlyIncreaseTextView = findViewById(R.id.monthly_interest_text_view);
        mBalanceTextView = findViewById(R.id.balance_text_view);
        mExpandedTextLayout = findViewById(R.id.expanded_text_layout);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mEditSavingsFAB = findViewById(R.id.editSavingsFAB);
        mRecyclerView = findViewById(R.id.recyclerview);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

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
                    mCollapsingToolbarLayout.setTitle(getApplicationName(SavingsIncomeDetailsActivity.this));
                } else {
                    isShow = false;
                    mExpandedTextLayout.setVisibility(View.VISIBLE);
                    mCollapsingToolbarLayout.setTitle("");
                }
            }
        });

        //mAppBarLayout.addOnOffsetChangedListener(new ScrollingHelper(mAppBarLayout.getTotalScrollRange(), this));

        mIncomeDetails = new ArrayList<>();
        mAdapter = new IncomeDetailsAdapter(this, mIncomeDetails);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // The FAB will pop up an activity to allow a new income source to be edited
        mEditSavingsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SavingsIncomeDetailsActivity.this, SavingsIncomeEditActivity.class);
                intent.putExtra(EXTRA_INCOME_SOURCE_ID, mId);
                intent.putExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, RetirementConstants.ACTIVITY_RESULT);
                startActivityForResult(intent, RetirementConstants.ACTIVITY_RESULT);
            }
        });

        SavingsIncomeViewModel.Factory factory = new
                SavingsIncomeViewModel.Factory(getApplication(), mId, 0);
        mViewModel = ViewModelProviders.of(this, factory).
                get(SavingsIncomeViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<IncomeDetails>>() {
            @Override
            public void onChanged(@Nullable List<IncomeDetails> incomeDetails) {
                mAdapter.update(incomeDetails);
            }
        });

        mViewModel.get().observe(this, new Observer<SavingsData>() {
            @Override
            public void onChanged(@Nullable SavingsData tdie) {
                mSIE = tdie;
                updateUI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == RetirementConstants.ACTIVITY_RESULT) {
            Bundle bundle = intent.getExtras();
            String name = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
            AgeData startAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE);
            String balance = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE);
            String interest = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST);
            String monthlyAddition = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_INCREASE);
            AgeData stopMonthlyAddtionAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_STOP_MONTHLY_ADDITION_AGE);
            String withdrawAmount = bundle.getString(RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT);
            String annualPercentIncrease = bundle.getString(RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE);
            int showMonths = bundle.getInt(RetirementConstants.EXTRA_INCOME_SHOW_MONTHS);

            SavingsData sdata = new SavingsData(mId, mSIE.getType(), name, startAge,
                    balance, interest, monthlyAddition, stopMonthlyAddtionAge,
                    withdrawAmount, annualPercentIncrease, showMonths);
            mViewModel.setData(sdata);

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void updateUI() {
        if(mSIE == null) {
            return;
        }

        SystemUtils.setToolbarSubtitle(this, "401(k) - " + mSIE.getName());

        mNameTextView.setText(mSIE.getName());

        AgeData age = mSIE.getStartAge();
        mStartAgeTextView.setText(age.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mSIE.getMonthlyAddition());
        mMonthlyIncreaseTextView.setText(formattedValue);

        formattedValue = mSIE.getInterest() + "%";
        mAnnualInterestTextView.setText(formattedValue);

        formattedValue = SystemUtils.getFormattedCurrency(mSIE.getBalance());
        mBalanceTextView.setText(formattedValue);
    }
/*
    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(this, RetirementDetailsActivity.class);
        intent.putExtra("milestone", msd);
        startActivity(intent);
    }
    */

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
