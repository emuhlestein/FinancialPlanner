package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.MilestoneData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MilestoneDetailsDialog extends AppCompatActivity {
    @Bind(R.id.milestone_age_text_view) TextView mAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;
    @Bind(R.id.balance_text_view) TextView mBalance;
    @Bind(R.id.info_text_view) TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_details_dialog);
        ButterKnife.bind(this);
        updateUI();
    }

    private void updateUI() {
        Intent intent = getIntent();
        MilestoneData msd = intent.getParcelableExtra(RetirementConstants.EXTRA_MILESTONEDATA);
        mAge.setText(SystemUtils.getFormattedAge(msd.getAge()));
        String formattedCurrency = SystemUtils.getFormattedCurrency(msd.getAmount());
        if(msd.getIncludesPenalty() == 1) {
            formattedCurrency = formattedCurrency + "*";
            // TODO use real penalty
            mInfoText.setText("Amount includes 10% penalty");
        }
        mMonthlyAmount.setText(formattedCurrency);
        mBalance.setText(SystemUtils.getFormattedCurrency(msd.getBalance()));
    }
}
