package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.ui.PersonalInfoDialog;
import com.intelliviz.retirementhelper.ui.RetirementOptionsDialog;
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.MilestoneData;
import com.intelliviz.retirementhelper.util.PersonalInfoData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;


public class ViewTaxDeferredIncomeFragment extends Fragment {
    public static final String VIEW_TAXDEF_INCOME_FRAG_TAG = "view taxdef income frag tag";
    private TaxDeferredIncomeData mTDID;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;
    @Bind(R.id.minimum_age_text_view) TextView mMinimumAge;
    @Bind(R.id.penalty_amount_text_view) TextView mPenaltyAmount;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;


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
            mTDID = intent.getParcelableExtra(RetirementConstants.EXTRA_INCOME_DATA);
        }
        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.summary_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.retirement_options_item:
                intent = new Intent(getContext(), RetirementOptionsDialog.class);
                RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(getContext());
                if (rod != null) {
                    intent.putExtra(RetirementConstants.EXTRA_RETIRMENTOPTIONSDATA, rod);
                }
                startActivityForResult(intent, REQUEST_RETIRE_OPTIONS);
                break;
            case R.id.personal_info_item:
                intent = new Intent(getContext(), PersonalInfoDialog.class);
                PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(getContext());
                if (pid != null) {
                    intent.putExtra(RetirementConstants.EXTRA_PERSONALINFODATA, pid);
                }
                startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        if(mTDID == null) {
            return;
        }

        mIncomeSourceName.setText(mTDID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mTDID.getType());
        SystemUtils.setToolbarSubtitle((AppCompatActivity)getActivity(), subTitle);
        mAnnualInterest.setText(mTDID.getInterest()+"%");
        // TODO getMonthAddition vs getMonthlyIncrease in SID
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mTDID.getMonthAddition()));
        mMinimumAge.setText(mTDID.getMinimumAge());
        mPenaltyAmount.setText(mTDID.getPenalty() +"%");

        List<BalanceData> bd = mTDID.getBalanceDataList();
        String formattedAmount = "$0.00";
        if(bd != null && !bd.isEmpty()) {
            formattedAmount = SystemUtils.getFormattedCurrency(bd.get(0).getBalance());
        }

        mCurrentBalance.setText(String.valueOf(formattedAmount));

        List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mTDID);
        String monthlyAmount = "0.00";
        if(!milestones.isEmpty()) {
            monthlyAmount = milestones.get(0).getAmount();
        }
         //BenefitHelper.getMonthlyBenefit(getContext(), mTDID);
        formattedAmount = SystemUtils.getFormattedCurrency(monthlyAmount);

        mMonthlyAmount.setText(formattedAmount);
    }
}
