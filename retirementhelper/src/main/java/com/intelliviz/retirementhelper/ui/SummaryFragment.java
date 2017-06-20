package com.intelliviz.retirementhelper.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.MilestoneAdapter;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementInfoMgr;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.DIALOG_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BUNDLE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;

public class SummaryFragment extends Fragment implements SelectionMilestoneListener {
    private RetirementOptionsData mROD;
    private PersonalInfoData mPERID;
    private MilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalanceTextView;

    private BroadcastReceiver mRetirementOptionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
            mROD = intent.getParcelableExtra(EXTRA_DB_DATA);
            List<MilestoneData> milestones = BenefitHelper.getAllMilestones(getContext(), mROD, mPERID);
            mMilestoneAdapter.update(milestones);
        }
    };

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(Bundle bundle) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BUNDLE, bundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments().getParcelable(EXTRA_BUNDLE);
            if(bundle != null) {
                mROD = bundle.getParcelable(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
                mPERID = bundle.getParcelable(RetirementConstants.EXTRA_PERSONALINFODATA);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        List<MilestoneData> milestones = BenefitHelper.getAllMilestones(getContext(), mROD, mPERID);
        if(!milestones.isEmpty()) {
            double currentBalance = milestones.get(0).getStartBalance();
            mMilestoneAdapter = new MilestoneAdapter(getContext(), milestones);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(mMilestoneAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation()));
            mMilestoneAdapter.setOnSelectionMilestoneListener(this);

            updateUI(currentBalance);

            DataBaseUtils.updateSummaryData(getContext());
        }

        String birthdate = RetirementInfoMgr.getInstance().getBirthdate();
        if(!SystemUtils.validateBirthday(birthdate) ) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BirthdateDialog dialog = BirthdateDialog.newInstance("Please enter your birth date");
            dialog.setTargetFragment(SummaryFragment.this, REQUEST_BIRTHDATE);
            dialog.show(fm, DIALOG_BIRTHDATE);
        }

        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BIRTHDATE:
                    onHandleBirthdate(intent);
                    break;
            }
        }
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

    private void updateUI(double currentBalance) {
        String formattedAmount = SystemUtils.getFormattedCurrency(currentBalance);
        mCurrentBalanceTextView.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestoneListener(MilestoneData msd) {

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_RETIRE_OPTIONS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRetirementOptionsReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRetirementOptionsReceiver);
    }

    private void onHandleBirthdate(Intent intent) {
        String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
        if(!SystemUtils.validateBirthday(birthdate)) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BirthdateDialog dialog = BirthdateDialog.newInstance("Please enter a valid birth date");
            dialog.setTargetFragment(SummaryFragment.this, REQUEST_BIRTHDATE);
            dialog.show(fm, DIALOG_BIRTHDATE);
            return;
        }

        SystemUtils.updatePERID(getContext(), new PersonalInfoData(birthdate));
    }
}
