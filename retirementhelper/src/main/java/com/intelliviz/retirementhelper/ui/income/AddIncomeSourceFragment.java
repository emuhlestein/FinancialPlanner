package com.intelliviz.retirementhelper.ui.income;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment used for adding and editing (savings) income sources.
 *
 * @author Ed Muhlestein
 */
public class AddIncomeSourceFragment extends Fragment {
    public static final String EDIT_INCOME_FRAG_TAG = "edit income frag tag";
    private long mIncomeSourceId;
    private int mIncomeSourceType;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public AddIncomeSourceFragment() {
        // Required empty public constructor
    }

    public static AddIncomeSourceFragment newInstance(long incomeSourceId) {
        AddIncomeSourceFragment fragment = new AddIncomeSourceFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_income_source, container, false);
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

        Cursor cursor = DataBaseUtils.getIncomeSource(getContext(), mIncomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_TYPE);
        String incomeSourceName = cursor.getString(nameIndex);
        mIncomeSourceType = cursor.getInt(typeIndex);
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), mIncomeSourceType);

        cursor = DataBaseUtils.getSavingsData(getContext(), mIncomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }
        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_INTEREST);
        int monthlyIncreaseIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION);
        float interest = cursor.getFloat(interestIndex);
        float monthlyIncrease = cursor.getFloat(monthlyIncreaseIndex);
        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(monthlyIncrease);
        String interestString = String.valueOf(interest);

        cursor = DataBaseUtils.getBalances(getContext(), mIncomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }
        int balanceIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_AMOUNT);
        float balance = cursor.getFloat(balanceIndex);
        String balanceString = SystemUtils.getFormattedCurrency(balance);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setSubtitle(incomeSourceTypeString);
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(interestString);
        mMonthlyIncrease.setText(monthlyIncreaseString);
    }

    public void sendIncomeSourceData() {
        Intent returnIntent = new Intent();

        String name = mIncomeSourceName.getText().toString();

        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, name);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);

        float balance = Float.parseFloat(mBalance.getText().toString());
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, balance);

        float interest = Float.parseFloat(mAnnualInterest.getText().toString());
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, interest);

        float monthlyIncrease = Float.parseFloat(mMonthlyIncrease.getText().toString());
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_INCREASE, monthlyIncrease);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
