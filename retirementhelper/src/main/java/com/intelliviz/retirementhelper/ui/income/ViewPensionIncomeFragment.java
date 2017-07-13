package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPensionIncomeFragment extends Fragment {
    public static final String VIEW_PENSION_INCOME_FRAG_TAG = "view pension income frag tag";
    private PensionIncomeData mPID;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.minimum_age_text_view) TextView mStartAge;
    @Bind(R.id.monthly_benefit_text_view) TextView mMonthlyBenefit;

    public static ViewPensionIncomeFragment newInstance(Intent intent) {
        ViewPensionIncomeFragment fragment = new ViewPensionIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
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
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            if(intent != null) {
                mPID = intent.getParcelableExtra(EXTRA_INCOME_DATA);
            }
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

        mIncomeSourceName.setText(mPID.getName());
        mStartAge.setText(mPID.getStartAge());

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mPID.getMonthlyBenefit(0));
        mMonthlyBenefit.setText(monthlyIncreaseString);

        int type = mPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
    }
}
