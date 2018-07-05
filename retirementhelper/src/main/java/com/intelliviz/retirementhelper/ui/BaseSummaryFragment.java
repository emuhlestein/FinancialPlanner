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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.adapter.IncomeDetailsAdapter;
import com.intelliviz.income.data.MilestoneData;
import com.intelliviz.income.viewmodel.IncomeSummaryViewModel;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.SummaryMilestoneAdapter;
import com.intelliviz.retirementhelper.util.UpdateRetirementOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Ed Muhlestein
 * Created on 8/7/2017.
 */

public abstract class BaseSummaryFragment extends Fragment implements
        SummaryMilestoneAdapter.SelectionMilestoneListener, UpdateRetirementOptions {
    private static final String DIALOG_INPUT_TEXT = "DialogInputText";
    private IncomeDetailsAdapter mIncomeDetailsAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private IncomeSummaryViewModel mViewModel;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

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

        mIncomeDetails = new ArrayList<>();
        mIncomeDetailsAdapter = new IncomeDetailsAdapter(getContext(), mIncomeDetails);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeDetailsAdapter);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
        //        linearLayoutManager.getOrientation()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(IncomeSummaryViewModel.class);

        mViewModel.getList().observe(this, new Observer<List<IncomeData>>() {
            @Override
            public void onChanged(@Nullable List<IncomeData> incomeDataList) {
                if(incomeDataList == null) {
                    return;
                }

                List<IncomeDetails> incomeDetails = new ArrayList<>();

                for(IncomeData benefitData : incomeDataList) {
                    AgeData age = benefitData.getAge();
                    String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
                    String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
                    String line1 = age.toString() + "   " + amount + "  " + balance;
                    IncomeDetails incomeDetail = new IncomeDetails(line1, RetirementConstants.BALANCE_STATE_GOOD, "");
                    incomeDetails.add(incomeDetail);
                }
                mIncomeDetailsAdapter.update(incomeDetails);
                if (incomeDataList.isEmpty()) {

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

    @Override
    public void updateRetirementOptions(RetirementOptionsEntity roe) {
        mViewModel.update();
    }
}
