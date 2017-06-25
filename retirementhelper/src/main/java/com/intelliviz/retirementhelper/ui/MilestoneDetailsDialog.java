package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MilestoneDetailsDialog extends AppCompatActivity {
    @Bind(R.id.milestone_age_text_view) TextView mAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;
    @Bind(R.id.start_balance_text_view) TextView mStartBalance;
    @Bind(R.id.final_balance_text_view) TextView mFinalBalance;
    @Bind(R.id.retire_length_text_view) TextView mRetirementDuration;
    @Bind(R.id.money_last_text_view) TextView mFundsDuration;
    @Bind(R.id.info_text_view) TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_details_dialog);
        ButterKnife.bind(this);
        updateUI();
    }

    private void updateUI() {
        StringBuilder sb = new StringBuilder();
        Intent intent = getIntent();
        MilestoneData msd = intent.getParcelableExtra(RetirementConstants.EXTRA_MILESTONEDATA);
        mAge.setText(SystemUtils.getFormattedAge(msd.getStartAge()));
        String formattedCurrency = SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit());
        double penaltyAmount = msd.getPenaltyAmount();
        if(penaltyAmount > 0) {
            sb.append("*");
            sb.append(msd.getPenaltyAmount());
            sb.append("% ");
            sb.append("penalty applies before minimum age is reached.");

            double monthlyAmount = msd.getMonthlyBenefit();
            double monthlyPenalty = monthlyAmount * penaltyAmount / 100.0;
            monthlyAmount = monthlyAmount - monthlyPenalty;
            formattedCurrency = SystemUtils.getFormattedCurrency(monthlyAmount);
            formattedCurrency = formattedCurrency + "*";
        }

        String finalBalance = Double.toString(msd.getEndBalance());
        finalBalance = SystemUtils.getFormattedCurrency(finalBalance);
        if(msd.getEndBalance() < 0) {
            finalBalance = "$0.00**";
            if(sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("**");
            sb.append("You do not have sufficient funds for the monthly amount desired.");
        }
        mInfoText.setText(sb.toString());
        mMonthlyAmount.setText(formattedCurrency);
        AgeData endAge = msd.getEndAge();
        AgeData startAge = msd.getStartAge();
        AgeData diffAge = endAge.subtract(startAge);
        mRetirementDuration.setText(diffAge.toString());

        int numMonths = msd.getMonthsFundsFillLast();
        int years = numMonths / 12;
        int months = numMonths - years * 12;
        AgeData age = new AgeData(






                years, months);
        mFundsDuration.setText(age.toString());

        mStartBalance.setText(SystemUtils.getFormattedCurrency(msd.getStartBalance()));
        mFinalBalance.setText(finalBalance);
    }
}
