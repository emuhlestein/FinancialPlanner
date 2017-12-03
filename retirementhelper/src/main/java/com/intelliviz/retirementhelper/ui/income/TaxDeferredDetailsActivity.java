package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.AmountData;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.ui.RetirementDetailsActivity;
import com.intelliviz.retirementhelper.util.SelectMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class TaxDeferredDetailsActivity extends AppCompatActivity
        implements SelectMilestoneDataListener {

    private IncomeDetailsAdapter mAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private TaxDeferredDetailsViewModel mViewModel;
    private TaxDeferredIncomeEntity mTDIE;
    private long mId;

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.appbar)
    android.support.design.widget.AppBarLayout mAppBarLayout;

    @BindView(R.id.expanded_text_view)
    TextView mExpandedTextView;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    //@BindView(R.id.annual_interest_text_view)
    //TextView mAnnualInterest;

    //@BindView(R.id.monthly_increase_text_view)
    //TextView mMonthlyIncrease;

    //@BindView(R.id.info_text_view)
    //TextView mInfoText;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_deferred_details);
        ButterKnife.bind(this);

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
                    mExpandedTextView.setVisibility(View.GONE);
                    mCollapsingToolbarLayout.setTitle(getApplicationName(TaxDeferredDetailsActivity.this));
                } else {
                    isShow = false;
                    mExpandedTextView.setVisibility(View.VISIBLE);
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

        TaxDeferredDetailsViewModel.Factory factory = new
                TaxDeferredDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredDetailsViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<AmountData>>() {
            @Override
            public void onChanged(@Nullable List<AmountData> taxDeferredData) {
                List<IncomeDetails> incomeDetails = new ArrayList<>();
                for(AmountData taxDefData : taxDeferredData) {
                    AgeData age = taxDefData.getAge();
                    String balance = SystemUtils.getFormattedCurrency(taxDefData.getBalance());
                    String amount = SystemUtils.getFormattedCurrency(taxDefData.getMonthlyAmount());
                    String line1 = age.toString() + "   " + amount + "  " + balance;
                    IncomeDetails incomeDetail = new IncomeDetails(line1, taxDefData.getBalanceState());
                    incomeDetails.add(incomeDetail);
                }
                mAdapter.update(incomeDetails);
            }
        });

        mViewModel.get().observe(this, new Observer<TaxDeferredIncomeEntity>() {
            @Override
            public void onChanged(@Nullable TaxDeferredIncomeEntity tdie) {
                mTDIE = tdie;
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
        if(mTDIE == null) {
            return;
        }

        SystemUtils.setToolbarSubtitle(this, "401(k) - " + mTDIE.getName());

        //mInfoText.setText("There is a 10% penalty for withdrawing funds from a 401(k) before age 59y 6m.");
        String formattedInterest = mTDIE.getInterest() + "%";
        //mAnnualInterest.setText(formattedInterest);
        String formattedCurrency = SystemUtils.getFormattedCurrency(mTDIE.getMonthlyIncrease());
        //mMonthlyIncrease.setText(formattedCurrency);
        AgeData penaltyAge = SystemUtils.parseAgeString(mTDIE.getMinAge());
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(this, RetirementDetailsActivity.class);
        intent.putExtra("milestone", msd);
        startActivity(intent);
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
