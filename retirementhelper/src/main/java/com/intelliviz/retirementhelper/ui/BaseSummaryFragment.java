package com.intelliviz.retirementhelper.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.intelliviz.retirementhelper.adapter.SummaryMilestoneAdapter;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.viewmodel.MilestoneSummaryViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Ed Muhlestein
 * Created on 8/7/2017.
 */

public abstract class BaseSummaryFragment extends Fragment implements
        SummaryMilestoneAdapter.SelectionMilestoneListener{
    private static final String DIALOG_INPUT_TEXT = "DialogInputText";
    private SummaryMilestoneAdapter mMilestoneAdapter;
    private List<MilestoneData> mMilestones;
    private MilestoneSummaryViewModel mViewModel;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalanceTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);

        createBannerAdd();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.summary_screen_subtitle));
        }

        mMilestones = new ArrayList<>();
        mMilestoneAdapter = new SummaryMilestoneAdapter(getContext(), mMilestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MilestoneSummaryViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<MilestoneData>>() {
            @Override
            public void onChanged(@Nullable List<MilestoneData> milestones) {
                mMilestoneAdapter.update(milestones);
                if (milestones.isEmpty()) {

                    final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.add_income_source_message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    public void createBannerAdd() {

    }

    @Override
    public void onSelectMilestone(MilestoneData milestone) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, milestone);
        startActivity(intent);
    }
}
