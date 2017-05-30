package com.intelliviz.retirementhelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.MilestoneData;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.MilestoneHolder> {
    private List<MilestoneData> mMilestones;
    private SelectionMilestoneListener mListener;

    public MilestoneAdapter(List<MilestoneData> milestones) {
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

    public void setOnSelectionMilestoneListener (SelectionMilestoneListener listener) {
        mListener = listener;
    }

    public class MilestoneHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private MilestoneData mMSD;

        public MilestoneHolder(View itemView) {
            super(itemView);
            mMilestoneTextView = (TextView) itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = (TextView) itemView.findViewById(R.id.monthly_amount_text_view);
            itemView.setOnClickListener(this);
        }

        public void bindMilestone(int position) {
            mMSD = mMilestones.get(position);
            String formattedCurrency = SystemUtils.getFormattedCurrency(mMSD.getAmount());
            if(mMSD.getIncludesPenalty() == 1) {
                formattedCurrency = formattedCurrency+"*";
            }
            mMilestoneTextView.setText(SystemUtils.getFormattedAge(mMSD.getAge()));
            mMonthlyAmountTextView.setText(formattedCurrency);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestoneListener(mMSD);
            }
        }
    }
}
