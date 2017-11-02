package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeViewDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.SelectMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class TaxDeferredDetailsActivity extends AppCompatActivity
        implements SelectMilestoneDataListener{

    private IncomeViewDetailsAdapter mAdapter;
    private List<MilestoneData> mMilestones;
    private TaxDeferredDetailsViewModel mViewModel;
    private TaxDeferredIncomeEntity mTDIE;
    private long mId;

    @Bind(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalance;

    @Bind(R.id.annual_interest_text_view)
    TextView mAnnualInterest;

    @Bind(R.id.monthly_increase_text_view)
    TextView mMonthlyIncrease;

    @Bind(R.id.info_text_view)
    TextView mInfoText;

    @Bind(R.id.recyclerview)
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

        mMilestones = new ArrayList<>();
        mAdapter = new IncomeViewDetailsAdapter(this, mMilestones);
        mAdapter.setOnSelectMilestoneDataListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));

        TaxDeferredDetailsViewModel.Factory factory = new
                TaxDeferredDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredDetailsViewModel.class);

        mViewModel.getMilestones().observe(this, new Observer<List<MilestoneData>>() {
            @Override
            public void onChanged(@Nullable List<MilestoneData> milestones) {
                mAdapter.update(milestones);
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

        SystemUtils.setToolbarSubtitle(this, "401(k)");

        mInfoText.setText("There is a 10% penalty for withdrawing funds from a 401(k) before age 59y 6m.");
        mIncomeSourceName.setText(mTDIE.getName());
        String formattedCurrency = SystemUtils.getFormattedCurrency(mTDIE.getBalance());
        mCurrentBalance.setText(formattedCurrency);
        String formattedInterest = mTDIE.getInterest() + "%";
        mAnnualInterest.setText(formattedInterest);
        formattedCurrency = SystemUtils.getFormattedCurrency(mTDIE.getMonthlyIncrease());
        mMonthlyIncrease.setText(formattedCurrency);
        AgeData penaltyAge = SystemUtils.parseAgeString(mTDIE.getMinAge());
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        // TODO start activity to show monthly benefits through life of retirement
    }
}
