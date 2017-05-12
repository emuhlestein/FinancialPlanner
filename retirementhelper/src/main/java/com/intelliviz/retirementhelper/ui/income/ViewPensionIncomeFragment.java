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
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.PensionIncomeData;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPensionIncomeFragment extends Fragment {
    public static final String VIEW_PENSION_INCOME_FRAG_TAG = "view pension income frag tag";
    private long mIncomeId;
    private int mIncomeType;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.age_text_view) TextView mStartAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyBenefit;

    public static ViewPensionIncomeFragment newInstance(long incomeSourceId) {
        ViewPensionIncomeFragment fragment = new ViewPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewPensionIncomeFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_view_pension_income, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        return view;
    }

    private void updateUI() {
        PensionIncomeData pid = DataBaseUtils.getPensionIncomeData(getContext(), mIncomeId);
        if(pid == null) {
            return;
        }

        mIncomeSourceName.setText(pid.getName());
        mStartAge.setText(pid.getStartAge());
        mMonthlyBenefit.setText(pid.getMonthlyBenefit());
    }

    private void setToolbarSubtitle(String subtitle) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }
}
