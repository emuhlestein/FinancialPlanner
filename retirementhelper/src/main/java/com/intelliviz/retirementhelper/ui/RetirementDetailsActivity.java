package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.RetirementDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RetirementDetailsActivity extends AppCompatActivity implements SelectAmountListener {

    private RetirementDetailsAdapter mRetirementDetailsAdapter;
    private List<BenefitData> mBenefitData;
    private MilestoneData mMSD;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.current_balance_text_view)
    TextView mCurrentBalanceTextView;

    @BindView(R.id.retirement_details_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirement_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mMSD = intent.getParcelableExtra("milestone");

        mBenefitData = new ArrayList<>();
        mRetirementDetailsAdapter = new RetirementDetailsAdapter(this, mBenefitData);
        mRetirementDetailsAdapter.setOnSelectAmountListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mRetirementDetailsAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));

        update();
    }

    private void update() {
        AgeData startAge = mMSD.getStartAge();
        AgeData endAge = mMSD.getEndAge();
        double balance = mMSD.getStartBalance();
        int withdrawMode = mMSD.getWithdrawMode();
        double withdrawAmount = mMSD.getWithdrawAmount();
        double interest = mMSD.getInterest();

        mCurrentBalanceTextView.setText(SystemUtils.getFormattedCurrency(balance));

        List<BenefitData> benefitDataList = new ArrayList<>();
        AgeData stopAge = new AgeData(startAge.getYear(), startAge.getMonth());
        stopAge.addMonths(12);
        for(AgeData age = startAge; age.isBefore(endAge); age.addMonths(12)) {
            BenefitData benefitData = BalanceUtils.getBenefitData(age, stopAge, interest, balance, withdrawMode, withdrawAmount);
            balance = benefitData.getBalance();
            benefitDataList.add(benefitData);
            stopAge.addMonths(12);
        }

        mRetirementDetailsAdapter.update(benefitDataList);
    }

    @Override
    public void onSelectAmount(BenefitData benefitData) {
        String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
        String message = "Balance: " + balance + "\nAge: " + benefitData.getAge().toString();
        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
