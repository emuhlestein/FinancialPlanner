package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.MilestoneHolder> {
    private List<MilestoneData> mMilestones;
    private SelectionMilestoneListener mListener;
    private Context mContext;

    public MilestoneAdapter(Context context, List<MilestoneData> milestones) {
        mContext = context;
        mMilestones = milestones;
    }

    @Override
    public MilestoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_item_layout, parent, false);
        return new MilestoneHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneHolder holder, int position) {
        holder.bindMilestone(position);
    }

    @Override
    public int getItemCount() {
        if(mMilestones != null) {
            return mMilestones.size();
        } else {
            return 0;
        }
    }

    public void update(List<MilestoneData> milestones) {
        mMilestones.clear();
        mMilestones.addAll(milestones);
        notifyDataSetChanged();

    }

    public void setOnSelectionMilestoneListener (SelectionMilestoneListener listener) {
        mListener = listener;
    }

    class MilestoneHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private LinearLayout mLinearLayout;
        private MilestoneData mMSD;

        private MilestoneHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.milestone_item_layout);
            mMilestoneTextView = (TextView) itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = (TextView) itemView.findViewById(R.id.monthly_amount_text_view);
            itemView.setOnClickListener(this);
        }

        private void bindMilestone(int position) {
            mMSD = mMilestones.get(position);

            final int sdk = android.os.Build.VERSION.SDK_INT;
            double annualAmount = mMSD.getMonthlyBenefit() * 12;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if(mMSD.getEndBalance() == 0) {
                    mLinearLayout.setBackground( mContext.getResources().getDrawable(R.drawable.red_ripple_effect) );
                } else {
                    if(mMSD.getEndBalance() < annualAmount) {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                    } else {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                    }
                }
            } else {
                if(mMSD.getEndBalance() == 0) {
                    mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.red_ripple_effect));
                } else {
                    if(mMSD.getEndBalance() < annualAmount) {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                    } else {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                    }
                }
            }

            double monthlyAmount = mMSD.getMonthlyBenefit();
            String formattedCurrency = SystemUtils.getFormattedCurrency(monthlyAmount);
            double penalty = mMSD.getPenaltyAmount();
            if(penalty > 0) {
                double monthlyPenalty = monthlyAmount * penalty / 100.0;
                monthlyAmount = monthlyAmount - monthlyPenalty;
                formattedCurrency = SystemUtils.getFormattedCurrency(monthlyAmount);
                formattedCurrency = formattedCurrency + "*";
            }

            mMonthlyAmountTextView.setText(formattedCurrency);
            mMilestoneTextView.setText(SystemUtils.getFormattedAge(mMSD.getStartAge()));
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestoneListener(mMSD);
            }
        }
    }
}
