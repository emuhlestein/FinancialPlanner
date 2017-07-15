package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.SSMilestoneAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;

/**
 * Fragment used for viewing government pension income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewGovPensionIncomeFragment extends Fragment implements SelectionMilestoneListener {

    public static final String VIEW_GOV_PENSION_INCOME_FRAG_TAG = "view gov pension income frag tag";
    private GovPensionIncomeData mGPID;
    private RetirementOptionsData mROD;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.min_age_text_view)
    TextView mMinAge;

    @Bind(R.id.full_age_text_view)
    TextView mFullAge;

    @Bind(R.id.monthly_amount_text_view)
    TextView mMonthlyBenefit;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

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

        List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mGPID, mROD);
        SSMilestoneAdapter milestoneAdapter = new SSMilestoneAdapter(getContext(), milestones, mROD);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(milestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        milestoneAdapter.setOnSelectionMilestoneListener(this);

        updateUI();
        return view;
    }

    private void updateUI() {
        if(mGPID == null) {
            return;
        }

        mIncomeSourceName.setText(mGPID.getName());
        mMinAge.setText(mGPID.getStartAge());

        int birthYear = SystemUtils.getBirthYear(mROD.getBirthdate());
        AgeData fullAge = GovPensionHelper.getFullRetirementAge(birthYear);
        mFullAge.setText(fullAge.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mGPID.getMonthlyBenefit());
        mMonthlyBenefit.setText(formattedValue);

        int type = mGPID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {

    }
}
