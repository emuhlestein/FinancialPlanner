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
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.ui.RetirementDetailsActivity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.SavingsIncomeDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_401K;

public class SavingsIncomeDetailsActivity extends AppCompatActivity
        implements SelectMilestoneDataListener {

    private IncomeDetailsAdapter mAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private SavingsIncomeDetailsViewModel mViewModel;
    private SavingsIncomeEntity mSIE;
    private long mId;
    private int mSavingsType;

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

        // The FAB will pop up an activity to allow a new income source to be created.
        mEditTaxDeferredFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SavingsIncomeDetailsActivity.this, SavingsIncomeEditActivity.class);
                intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mId);
                intent.putExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, RetirementConstants.ACTIVITY_RESULT);
                startActivityForResult(intent, RetirementConstants.ACTIVITY_RESULT);
            }
        });

        SavingsIncomeDetailsViewModel.Factory factory = new
                SavingsIncomeDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(SavingsIncomeDetailsViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<AmountData>>() {
            @Override
            public void onChanged(@Nullable List<AmountData> amountDataList) {
                List<IncomeDetails> incomeDetails = new ArrayList<>();
                for(AmountData amountData : amountDataList) {
                    AgeData age = amountData.getAge();
                    String balance = SystemUtils.getFormattedCurrency(amountData.getBalance());
                    String amount = SystemUtils.getFormattedCurrency(amountData.getMonthlyAmount());
                    String line1 = age.toString() + "   " + amount + "  " + balance;

                    int status = amountData.getBalanceState();
                    if(amountData.isPenalty()) {
                        status = 0;
                    }
                    IncomeDetails incomeDetail = new IncomeDetails(line1, status, "");
                    incomeDetails.add(incomeDetail);
                }
                mAdapter.update(incomeDetails);
            }
        });

        mViewModel.get().observe(this, new Observer<SavingsIncomeEntity>() {
            @Override
            public void onChanged(@Nullable SavingsIncomeEntity tdie) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == RetirementConstants.ACTIVITY_RESULT) {
            String name = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
            AgeData startAge = data.getParcelableExtra(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE);
            String balance = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE);
            String interest = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST);
            String monthlyAddition = data.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INCREASE);
            SavingsIncomeEntity tdid = new SavingsIncomeEntity(mId, INCOME_TYPE_401K, name,
                    balance, interest, monthlyAddition, startAge);
            mViewModel.setData(tdid);

        }
        super.onActivityResult(requestCode, resultCode, data);
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
