package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * A simple {@link Fragment} subclass.
 */
public class ViewSavingsIncomeFragment extends Fragment {
    public static final String VIEW_SAVINGS_INCOME_FRAG_TAG = "view savings income frag tag";
    private long mIncomeId;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;

    /**
     * Create the ViewSavingsIncomeFragment.
     * @param incomeSourceId The database table id for selected income source.
     * @return The ViewSavingsIncomeFragment.
     */
    public static ViewSavingsIncomeFragment newInstance(long incomeSourceId) {
        ViewSavingsIncomeFragment fragment = new ViewSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSavingsIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeId = getArguments().getLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_savings_income, container, false);
        ButterKnife.bind(this, view);

        updateUI();

        return view;
    }

    private void updateUI() {
        SavingsIncomeData sid = DataBaseUtils.getSavingsIncomeData(getContext(), mIncomeId);
        if(sid == null) {
            return;
        }

        mIncomeSourceName.setText(sid.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), sid.getType());
        SystemUtils.setToolbarSubtitle((AppCompatActivity)getActivity(), subTitle);
        mAnnualInterest.setText(String.valueOf(sid.getInterest())+"%");
        mMonthlyIncrease.setText("$"+String.valueOf(sid.getMonthlyIncrease()));

        BalanceData[] bd = DataBaseUtils.getBalanceData(getContext(), mIncomeId);
        String formattedAmount = "$0.00";
        if(bd != null) {
            formattedAmount = SystemUtils.getFormattedCurrency(bd[0].getBalance());
        }

        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }
}
