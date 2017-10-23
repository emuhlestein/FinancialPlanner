package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeViewDetailsAdapter;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.viewmodel.GovPensionDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class GovPensionDetailsActivity extends AppCompatActivity {
    private IncomeViewDetailsAdapter mAdapter;
    private List<MilestoneData> mMilestones;
    private GovPensionDetailsViewModel mViewModel;
    private long mId;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_view_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mMilestones = new ArrayList<>();
        mAdapter = new IncomeViewDetailsAdapter(this, mMilestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));

        GovPensionDetailsViewModel.Factory factory = new
                GovPensionDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionDetailsViewModel.class);

        mViewModel.get().observe(this, new Observer<List<MilestoneData>>() {
            @Override
            public void onChanged(@Nullable List<MilestoneData> milestones) {
                mAdapter.update(milestones);
            }
        });
    }
}
