package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.SummaryMilestoneAdapter;
import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.ui.MilestoneDetailsDialog;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;

public class ViewTaxDeferredIncomeFragment extends Fragment implements SelectionMilestoneListener {
    public static final String VIEW_TAXDEF_INCOME_FRAG_TAG = "view taxdef income frag tag";
    private TaxDeferredIncomeData mTDID;
    private RetirementOptionsData mROD;
    private SummaryMilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;
    @Bind(R.id.minimum_age_text_view) TextView mMinimumAge;
    @Bind(R.id.penalty_amount_text_view) TextView mPenaltyAmount;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    private BroadcastReceiver mRetirementOptionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
            mROD = intent.getParcelableExtra(EXTRA_DB_DATA);
            List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mTDID, mROD);
            mMilestoneAdapter.update(milestones);
        }
    };

    public ViewTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static ViewTaxDeferredIncomeFragment newInstance(Intent intent) {
        ViewTaxDeferredIncomeFragment fragment = new ViewTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            if(intent != null) {
                mTDID = intent.getParcelableExtra(EXTRA_INCOME_DATA);
                mROD = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mTDID, mROD);
        mMilestoneAdapter = new SummaryMilestoneAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        updateUI();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void updateUI() {
        if(mTDID == null) {
            return;
        }

        mIncomeSourceName.setText(mTDID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mTDID.getType());
        SystemUtils.setToolbarSubtitle((AppCompatActivity)getActivity(), subTitle);
        // TODO deal with % sign here and below
        mAnnualInterest.setText(mTDID.getInterestRate()+"%");
        // TODO getMonthAddition vs getMonthlyIncrease in SID - spelling consistency
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mTDID.getMonthAddition()));
        mMinimumAge.setText(mTDID.getMinimumAge());
        mPenaltyAmount.setText(mTDID.getPenalty() +"%");

        List<BalanceData> bd = mTDID.getBalanceData();
        String formattedAmount = "$0.00";
        if(bd != null && !bd.isEmpty()) {
            formattedAmount = SystemUtils.getFormattedCurrency(bd.get(0).getBalance());
        }

        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_RETIRE_OPTIONS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRetirementOptionsReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRetirementOptionsReceiver);
    }
}
