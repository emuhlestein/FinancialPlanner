package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 11/1/2017.
 */

public class RetirementDetailsAdapter {
    private Context mContext;
    public RetirementDetailsAdapter(Context context) {
        mContext = context;
    }

    class MilestoneHolder extends RecyclerView.ViewHolder {
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private LinearLayout mLinearLayout;
        private MilestoneData mMilestone;

        private MilestoneHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.milestone_item_layout);
            mMilestoneTextView = itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = itemView.findViewById(R.id.monthly_amount_text_view);
        }

        private void bindMilestone(MilestoneData milestone) {

            mMilestone = milestone;
            double monthlyBenefit = milestone.getMonthlyBenefit();
            double endBalance = milestone.getEndBalance();
            double penalty = milestone.getPenaltyAmount();
            AgeData startAge = milestone.getStartAge();

            final int sdk = android.os.Build.VERSION.SDK_INT;
            double annualAmount = monthlyBenefit * 12;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if(endBalance == 0) {
                    mLinearLayout.setBackground( mContext.getResources().getDrawable(R.drawable.red_ripple_effect) );
                } else {
                    if(endBalance < annualAmount) {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                    } else {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                    }
                }
            } else {
                if(endBalance == 0) {
                    mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.red_ripple_effect));
                } else {
                    if(endBalance < annualAmount) {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                    } else {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                    }
                }
            }

            String formattedCurrency = SystemUtils.getFormattedCurrency(monthlyBenefit);
            if(penalty > 0) {
                double monthlyPenalty = monthlyBenefit * penalty / 100.0;
                monthlyBenefit = monthlyBenefit - monthlyPenalty;
                formattedCurrency = SystemUtils.getFormattedCurrency(monthlyBenefit);
                formattedCurrency = formattedCurrency + "*";
            }

            mMonthlyAmountTextView.setText(formattedCurrency);
            mMilestoneTextView.setText(SystemUtils.getFormattedAge(startAge));
        }
    }
}
