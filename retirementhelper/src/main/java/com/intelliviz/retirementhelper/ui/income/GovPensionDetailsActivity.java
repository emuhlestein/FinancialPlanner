package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionData;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.GovPensionDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class GovPensionDetailsActivity extends AppCompatActivity {
    private IncomeDetailsAdapter mAdapter;
    private List<IncomeDetails> mIncomeDetails;
    private GovPensionDetailsViewModel mViewModel;
    private long mId;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gov_pension_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mIncomeDetails = new ArrayList<>();
        mAdapter = new IncomeDetailsAdapter(this, mIncomeDetails);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // For adding dividing line between views
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
        //        linearLayoutManager.getOrientation()));
        GovPensionDetailsViewModel.Factory factory = new
                GovPensionDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionDetailsViewModel.class);

        mViewModel.get().observe(this, new Observer<List<GovPensionData>>() {
            @Override
            public void onChanged(@Nullable List<GovPensionData> govPensionData) {

                List<IncomeDetails> incomeDetails = new ArrayList<>();
                for(GovPensionData pensionData : govPensionData) {
                    AgeData age = pensionData.getAge();
                    String amount = SystemUtils.getFormattedCurrency(pensionData.getBenefit());
                    String line1 = age.toString() + "   " + amount;
                    IncomeDetails incomeDetail;
                    if(pensionData.isIncludeSpouse()) {
                        amount = SystemUtils.getFormattedCurrency(pensionData.getSpouseBenefit());
                        String     line2 = "Spouse: " + pensionData.getSpouseAge().toString() + " " + amount;
                        amount =  SystemUtils.getFormattedCurrency(pensionData.getBenefit() + pensionData.getSpouseBenefit());
                        String line3 = "Combined: " + amount;
                        incomeDetail = new IncomeDetails(line1, line2, line3, pensionData.getBenefitInfo());
                    } else {
                        incomeDetail = new IncomeDetails(line1, pensionData.getBenefitInfo());
                    }

                    incomeDetails.add(incomeDetail);
                }
                mAdapter.update(incomeDetails);
            }
        });
    }
}
