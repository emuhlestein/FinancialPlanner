package com.intelliviz.retirementhelper.ui.income;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.IncomeSourceData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsDataData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment used for adding and editing (savings) income sources.
 *
 * @author Ed Muhlestein
 */
public class EditSavingsIncomeFragment extends Fragment {
    public static final String EDIT_SAVINGS_INCOME_FRAG_TAG = "edit savings income frag tag";
    private long mIncomeSourceId;
    private int mIncomeSourceType;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public EditSavingsIncomeFragment() {
        // Required empty public constructor
    }

    public static EditSavingsIncomeFragment newInstance(long incomeSourceId) {
        EditSavingsIncomeFragment fragment = new EditSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeSourceId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_savings_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mIncomeSourceId == -1) {
            ab.setSubtitle("Savings");
        } else {
            updateUI();
        }
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        return view;
    }

    private void updateUI() {
        if(mIncomeSourceId == -1) {
            return;
        }

        IncomeSourceData isd = DataBaseUtils.getIncomeSourceData(getContext(), mIncomeSourceId);
        if(isd == null) {
            return;
        }

        String incomeSourceName = isd.getName();
        mIncomeSourceType = isd.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), mIncomeSourceType);

        String balanceString;
        BalanceData[] bd = DataBaseUtils.getBalanceData(getContext(), mIncomeSourceId);
        if(bd == null) {
            balanceString = "0.00";
        } else {
            balanceString = SystemUtils.getFormattedCurrency(bd[0].getBalance());
        }

        SavingsDataData sdd = DataBaseUtils.getSavingsDataData(getContext(), mIncomeSourceId);
        if(sdd == null) {
            return;
        }
        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(sdd.getMonthlyIncrease());
        String interestString = String.valueOf(sdd.getInterest());

        SystemUtils.setToolbarSubtitle((AppCompatActivity)getActivity(), incomeSourceTypeString);
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(interestString);
        mMonthlyIncrease.setText(monthlyIncreaseString);
    }

    public void sendIncomeSourceData() {
        String balance = SystemUtils.getCurrencyValue(mBalance);
        String interest = mAnnualInterest.getText().toString();
        String monthlyIncrease = SystemUtils.getCurrencyValue(mMonthlyIncrease);
        if(!SystemUtils.isValidFloatValue(balance)) {
            // TODO pop up error message
            return;
        }
        if(!SystemUtils.isValidFloatValue(interest)) {
            // TODO pop up error message
            return;
        }
        if(!SystemUtils.isValidFloatValue(monthlyIncrease)) {
            // TODO pop up error message
            return;
        }

        Intent returnIntent = new Intent();

        String name = mIncomeSourceName.getText().toString();
        String date = SystemUtils.getTodaysDate();

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE_DATE, date);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, interest);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_INCREASE, monthlyIncrease);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
