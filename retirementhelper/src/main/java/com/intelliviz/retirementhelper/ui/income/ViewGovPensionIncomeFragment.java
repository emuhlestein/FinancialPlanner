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
import com.intelliviz.retirementhelper.util.GovPensionIncomeData;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewGovPensionIncomeFragment extends Fragment {

    public static final String VIEW_GOV_PENSION_INCOME_FRAG_TAG = "view gov pension income frag tag";
    private long mIncomeId;
    private int mIncomeType;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.min_age_text_view) TextView mMinAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyBenefit;

    public static ViewGovPensionIncomeFragment newInstance(long incomeSourceId) {
        ViewGovPensionIncomeFragment fragment = new ViewGovPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
        return fragment;
    }
    public ViewGovPensionIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_gov_pension_income, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        return view;
    }

    private void updateUI() {
        GovPensionIncomeData gpid = DataBaseUtils.getGovPensionIncomeData(getContext(), mIncomeId);
        if(gpid == null) {
            return;
        }

        mIncomeSourceName.setText(gpid.getName());
        mMinAge.setText(gpid.getStartAge());
        mMonthlyBenefit.setText(gpid.getMonthlyBenefit());
    }

    private void setToolbarSubtitle(String subtitle) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

}
