package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.TaxDeferredData;
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
        implements SelectMilestoneDataListener{

    private IncomeDetailsAdapter mAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private TaxDeferredDetailsViewModel mViewModel;
    private TaxDeferredIncomeEntity mTDIE;
    private long mId;

    @BindView(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.name_text_view)
    TextView mIncomeSourceName;

    @BindView(R.id.current_balance_text_view)
    TextView mCurrentBalance;

    @BindView(R.id.annual_interest_text_view)
    TextView mAnnualInterest;

    @BindView(R.id.monthly_increase_text_view)
    TextView mMonthlyIncrease;

    @BindView(R.id.info_text_view)
    TextView mInfoText;

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

        mIncomeDetails = new ArrayList<>();
        mAdapter = new IncomeDetailsAdapter(this, mIncomeDetails);
        //mAdapter.setOnSelectMilestoneDataListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
        //        linearLayoutManager.getOrientation()));

        TaxDeferredDetailsViewModel.Factory factory = new
                TaxDeferredDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredDetailsViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<TaxDeferredData>>() {
            @Override
            public void onChanged(@Nullable List<TaxDeferredData> taxDeferredData) {
                List<IncomeDetails> incomeDetails = new ArrayList<>();
                for(TaxDeferredData taxDefData : taxDeferredData) {
                    AgeData age = taxDefData.getAge();
                    String balance = SystemUtils.getFormattedCurrency(taxDefData.getStartBalance());
                    String amount = SystemUtils.getFormattedCurrency(taxDefData.getWithdrawAmount());
                    String line1 = age.toString() + "   " + amount + "  " + balance;
                    String line2 = SystemUtils.getFormattedCurrency(taxDefData.getEndBalance()) + "  " +
                            SystemUtils.getFormattedCurrency(taxDefData.getFinalWithdrawAmount());
                    String line3 = "";
                    IncomeDetails incomeDetail = new IncomeDetails(line1, line2, line3, taxDefData.getStatus());
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
        Intent intent = new Intent(this, RetirementDetailsActivity.class);
        intent.putExtra("milestone", msd);
        startActivity(intent);
    }
}
