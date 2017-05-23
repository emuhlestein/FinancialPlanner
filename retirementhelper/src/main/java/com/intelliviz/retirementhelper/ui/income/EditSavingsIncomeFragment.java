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
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsIncomeData;
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
    private SavingsIncomeData mSID;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public EditSavingsIncomeFragment() {
        // Required empty public constructor
    }

    public static EditSavingsIncomeFragment newInstance(SavingsIncomeData sid) {
        EditSavingsIncomeFragment fragment = new EditSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(RetirementConstants.EXTRA_INCOME_SAVINGS, sid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSID = getArguments().getParcelable(RetirementConstants.EXTRA_INCOME_SAVINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_savings_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mSID.getId() == -1) {
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

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString = SystemUtils.getFormattedCurrency(textView.getText().toString());
                    if(formattedString == null) {
                        formattedString = "Invalid Number: " + textView.getText().toString();
                    }
                    mBalance.setText(formattedString);
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString = SystemUtils.getFormattedCurrency(textView.getText().toString());
                    if(formattedString == null) {
                        formattedString = "Invalid Number: " + textView.getText().toString();
                    }
                    mMonthlyIncrease.setText(formattedString);
                }
            }
        });

        return view;
    }

    private void updateUI() {
        if(mSID.getId() == -1) {
            return;
        }

        String incomeSourceName = mSID.getName();
        int type = mSID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);

        String balanceString;
        BalanceData[] bd = DataBaseUtils.getBalanceData(getContext(), mSID.getId());
        if(bd == null) {
            balanceString = "0.00";
        } else {
            balanceString = SystemUtils.getFormattedCurrency(bd[0].getBalance());
        }

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease());
        String interestString = String.valueOf(mSID.getInterest());

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

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE_DATE, date);

        SavingsIncomeData sid = new SavingsIncomeData(mSID.getId(), name, mSID.getType(), interest, monthlyIncrease);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SAVINGS, sid);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
