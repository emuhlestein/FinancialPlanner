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
import com.intelliviz.retirementhelper.data.AmountData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.BalanceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RetirementDetailsActivity extends AppCompatActivity implements SelectAmountListener {

    private RetirementDetailsAdapter mRetirementDetailsAdapter;
    private List<AmountData> mAmountData;
    private MilestoneData mMSD;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalanceTextView;

    @Bind(R.id.retirement_details_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirement_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mMSD = intent.getParcelableExtra("milestone");

        mAmountData = new ArrayList<>();
        mRetirementDetailsAdapter = new RetirementDetailsAdapter(this, mAmountData);
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

        List<AmountData> amountDatas = new ArrayList<>();
        AgeData stopAge = new AgeData(startAge.getYear(), startAge.getMonth());
        stopAge.addMonths(12);
        for(AgeData age = startAge; age.isBefore(endAge); age.addMonths(12)) {
            AmountData amountData = BalanceUtils.getAmountData(age, stopAge, interest, balance, withdrawMode, withdrawAmount);
            balance = amountData.getBalance();
            amountDatas.add(amountData);
            stopAge.addMonths(12);
        }

        mRetirementDetailsAdapter.update(amountDatas);
    }

    @Override
    public void onSelectAmount(AmountData amountData) {
        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Test Message", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
