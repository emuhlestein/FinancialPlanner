package com.intelliviz.retirementhelper.ui.income;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewIncomeSourceFragment extends Fragment {
    public static final String VIEW_INCOME_FRAG_TAG = "view income frag tag";
    private static final String INCOME_SOURCE_ID_PARAM = "income source id";
    private long mIncomeSourceId;
    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balnce_text_view) TextView mCurrentBalance;

    /**
     * Create the ViewIncomeSourceFragment.
     * @param incomeSourceId The database table id for selected income source.
     * @return The ViewIncomeSourceFragment.
     */
    public static ViewIncomeSourceFragment newInstance(long incomeSourceId) {
        ViewIncomeSourceFragment fragment = new ViewIncomeSourceFragment();
        Bundle args = new Bundle();
        args.putLong(INCOME_SOURCE_ID_PARAM, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewIncomeSourceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncomeSourceId = getArguments().getLong(INCOME_SOURCE_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_income_source, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    private void updateUI() {
        Cursor cursor = DataBaseUtils.getIncomeSource(getContext(), mIncomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }

        int sourceIncomeTypeIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_TYPE);
        int sourceIncomeNameIndex = cursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_NAME);
        int incomeSourceType = cursor.getInt(sourceIncomeNameIndex);
        String name = cursor.getString(sourceIncomeNameIndex);
        mIncomeSourceName.setText(name);

        cursor = DataBaseUtils.getSavingsData(getContext(), mIncomeSourceId);
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }
        int sourceInterestIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_INTEREST);
        int sourceMonthlyIndex = cursor.getColumnIndex(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION);
        float interest = cursor.getFloat(sourceInterestIndex);
        float monthlyIncreate = cursor.getFloat(sourceMonthlyIndex);
        mAnnualInterest.setText(String.valueOf(interest));
        mMonthlyIncrease.setText(String.valueOf(monthlyIncreate));
    }
}
