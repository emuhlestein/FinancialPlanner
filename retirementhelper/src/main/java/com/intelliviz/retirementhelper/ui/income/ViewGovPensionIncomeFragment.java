package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewGovPensionIncomeFragment extends Fragment {

    public static final String VIEW_GOV_PENSION_INCOME_FRAG_TAG = "view gov pension income frag tag";
    private GovPensionIncomeData mGPID;
    private RetirementOptionsData mROD;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.min_age_text_view) TextView mMinAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyBenefit;

    public static ViewGovPensionIncomeFragment newInstance(Intent intent) {
        ViewGovPensionIncomeFragment fragment = new ViewGovPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }
    public ViewGovPensionIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            if(intent != null) {
                mGPID = intent.getParcelableExtra(EXTRA_INCOME_DATA);
                mROD = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
            }
        }
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

        mIncomeSourceName.setText(mGPID.getName());
        mMinAge.setText(mGPID.getStartAge());

        // TODO need to format
        mMonthlyBenefit.setText(Double.toString(mGPID.getMonthlyBenefit(0)));
    }
}
