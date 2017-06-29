package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PERSONAL_DATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewGovPensionIncomeFragment extends Fragment implements SelectionMilestoneListener {

    public static final String VIEW_GOV_PENSION_INCOME_FRAG_TAG = "view gov pension income frag tag";
    private GovPensionIncomeData mGPID;
    private RetirementOptionsData mROD;
    private SSMilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.min_age_text_view) TextView mMinAge;
    @Bind(R.id.full_age_text_view) TextView mFullAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyBenefit;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    private BroadcastReceiver mPersonalInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
            mROD = intent.getParcelableExtra(EXTRA_DB_DATA);
            updateUI();
            //List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mTDID, mROD);
            //mMilestoneAdapter.update(milestones);
        }
    };

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
        mMilestoneAdapter = new SSMilestoneAdapter(getContext(), milestones, mROD);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        updateUI();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void updateUI() {

        mIncomeSourceName.setText(mGPID.getName());
        mMinAge.setText(mGPID.getStartAge());

        int birthYear = SystemUtils.getBirthYear(mROD.getBirthdate());
        AgeData fullAge = GovPensionHelper.getFullRetirementAge(birthYear);
        mFullAge.setText(fullAge.toString());

        // TODO need to format
        mMonthlyBenefit.setText(Double.toString(mGPID.getMonthlyBenefit(0)));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_PERSONAL_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPersonalInfoReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPersonalInfoReceiver);
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {

    }
}
