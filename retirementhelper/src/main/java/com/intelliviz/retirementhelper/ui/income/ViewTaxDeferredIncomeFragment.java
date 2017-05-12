package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ViewTaxDeferredIncomeFragment extends Fragment {
    public static final String VIEW_TAXDEF_INCOME_FRAG_TAG = "view taxdef income frag tag";
    private long mIncomeId;
    private int mIncomeType;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;
    @Bind(R.id.minimum_age_text_view) TextView mMinimumAge;
    @Bind(R.id.penalty_amount_text_view) TextView mPenaltyAmount;


    public ViewTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static ViewTaxDeferredIncomeFragment newInstance(long incomeSourceId) {
        ViewTaxDeferredIncomeFragment fragment = new ViewTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        return view;
    }

    private void updateUI() {
        TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), mIncomeId);
        if(tdid == null) {
            return;
        }

        mIncomeSourceName.setText(tdid.getName());
        setToolbarSubtitle(tdid.getName());
        mAnnualInterest.setText(String.valueOf(tdid.getInterest()));
        mMonthlyIncrease.setText(String.valueOf(tdid.getMonthAddition()));
        mMinimumAge.setText(tdid.getMinimumAge());
        mPenaltyAmount.setText(tdid.getPenalty() +"%");

        BalanceData[] bd = DataBaseUtils.getBalanceData(getContext(), mIncomeId);
        if(bd == null) {
            return;
        }

        String formattedAmount = SystemUtils.getFormattedCurrency(bd[0].getBalance());
        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }

    private void setToolbarSubtitle(String subtitle) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }
}
