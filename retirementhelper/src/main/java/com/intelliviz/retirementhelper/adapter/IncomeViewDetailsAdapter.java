package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.income.data.MilestoneData;
import com.intelliviz.income.util.SelectMilestoneDataListener;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.retirementhelper.R;

import java.util.List;

import static com.intelliviz.lowlevel.util.AgeUtils.getFormattedAge;
import static com.intelliviz.lowlevel.util.SystemUtils.getFormattedCurrency;

/**
 * Created by edm on 10/16/2017.
 */

public class IncomeViewDetailsAdapter extends RecyclerView.Adapter<IncomeViewDetailsAdapter.IncomeViewDetailsHolder>{
    private List<MilestoneData> mMilestones;
    private Context mContext;
    private SelectMilestoneDataListener mListener;

    public IncomeViewDetailsAdapter(Context context, List<MilestoneData> milestones) {
        mContext = context;
        mMilestones = milestones;
    }

    @Override
    public IncomeViewDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout_3line, parent, false);
        return new IncomeViewDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeViewDetailsHolder holder, int position) {
        MilestoneData milestone = mMilestones.get(position);
        holder.bindMilestone(milestone);
    }

    @Override
    public int getItemCount() {
        if(mMilestones != null) {
            return mMilestones.size();
        } else {
            return 0;
        }
    }

    public void setOnSelectMilestoneDataListener(SelectMilestoneDataListener listener) {
        mListener = listener;
    }

    public void update(List<MilestoneData> milestones) {
        if(milestones != null) {
            mMilestones.clear();
            mMilestones.addAll(milestones);
            notifyDataSetChanged();
        }
    }

    class IncomeViewDetailsHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mLine1TextView;
        private TextView mLine2TextView;
        private LinearLayout mLinearLayout;
        private MilestoneData mMilestone;

        public IncomeViewDetailsHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.gov_pension_item_layout);
            mLine1TextView = itemView.findViewById(R.id.line1);
            mLine2TextView = itemView.findViewById(R.id.line2);
            itemView.setOnClickListener(this);
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

            String formattedCurrency = getFormattedCurrency(monthlyBenefit);
            if(penalty > 0) {
                double monthlyPenalty = monthlyBenefit * penalty / 100.0;
                monthlyBenefit = monthlyBenefit - monthlyPenalty;
                formattedCurrency = getFormattedCurrency(monthlyBenefit);
                formattedCurrency = formattedCurrency + "*";
            }

            mLine2TextView.setText(formattedCurrency);
            mLine1TextView.setText(getFormattedAge(startAge));
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestone(mMilestone);
            }
        }
    }
}
