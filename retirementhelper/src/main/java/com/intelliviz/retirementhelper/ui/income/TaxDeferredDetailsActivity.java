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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.AmountData;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.ui.RetirementDetailsActivity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;

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

    @BindView(R.id.name_text_view)
    TextView mNameTextView;

    @BindView(R.id.start_age_text_view)
    TextView mStartAgeTextView;

    @BindView(R.id.annual_interest_text_view)
    TextView mAnnualInterestTextView;

    @BindView(R.id.monthly_interest_text_view)
    TextView mMonthlyIncreaseTextView;

    @BindView(R.id.balance_text_view)
    TextView mBalanceTextView;

    @BindView(R.id.expanded_text_layout)
    LinearLayout mExpandedTextLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.editTaxDeferredFAB)
    FloatingActionButton mEditTaxDeferredFAB;

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
                    mExpandedTextLayout.setVisibility(View.GONE);
                    mCollapsingToolbarLayout.setTitle(getApplicationName(TaxDeferredDetailsActivity.this));
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

        // The FAB will pop up an activity to allow a new income source to be created.
        mEditTaxDeferredFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaxDeferredDetailsActivity.this, TaxDeferredIncomeActivity.class);
                intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mId);
                intent.putExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, RetirementConstants.ACTIVITY_RESULT);
                startActivityForResult(intent, RetirementConstants.ACTIVITY_RESULT);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == RetirementConstants.ACTIVITY_RESULT) {
            String name = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
            String startAge = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE);
            String balance = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE);
            String interest = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST);
            String monthlyIncrease = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INCREASE);
            TaxDeferredIncomeEntity tdid = new TaxDeferredIncomeEntity(mId, INCOME_TYPE_TAX_DEFERRED, name,
                    interest, monthlyIncrease, "10", "59 6", 1, balance, startAge);
            mViewModel.setData(tdid);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        if(mTDIE == null) {
            return;
        }

        SystemUtils.setToolbarSubtitle(this, "401(k) - " + mTDIE.getName());

        mNameTextView.setText(mTDIE.getName());

        AgeData age = SystemUtils.parseAgeString(mTDIE.getStartAge());
        mStartAgeTextView.setText(age.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mTDIE.getMonthlyIncrease());
        mMonthlyIncreaseTextView.setText(formattedValue);

        formattedValue = mTDIE.getInterest() + "%";
        mAnnualInterestTextView.setText(formattedValue);

        formattedValue = SystemUtils.getFormattedCurrency(mTDIE.getBalance());
        mBalanceTextView.setText(formattedValue);
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
