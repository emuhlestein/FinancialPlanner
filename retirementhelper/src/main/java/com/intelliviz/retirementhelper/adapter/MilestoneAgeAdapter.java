package com.intelliviz.retirementhelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Adapter for milestones.
 * Created by Ed Muhlestein on 6/22/2017.
 */

public class MilestoneAgeAdapter extends RecyclerView.Adapter<MilestoneAgeAdapter.MilestoneAgeHolder> {
    private SelectMilestoneAgeListener mListener;
    private List<MilestoneAgeData> mMilestoneAges;

    public MilestoneAgeAdapter(List<MilestoneAgeData> milestoneAges) {
        mMilestoneAges = milestoneAges;
    }

    @Override
    public MilestoneAgeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_age_item, parent, false);
        return new MilestoneAgeHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneAgeHolder holder, int position) {
        MilestoneAgeData ageData = mMilestoneAges.get(position);
        holder.bindMilestone(ageData);
    }

    @Override
    public int getItemCount() {
        if(mMilestoneAges != null) {
            return mMilestoneAges.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void update(List<MilestoneAgeData> ages) {
        mMilestoneAges.clear();
        if(ages != null) {
            mMilestoneAges.addAll(ages);
        }
        notifyDataSetChanged();
    }

    public void setOnSelectMilestoneAgeListener(SelectMilestoneAgeListener listener) {
        mListener = listener;
    }

    class MilestoneAgeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private TextView mMilestoneAgeTextView;
        private MilestoneAgeData mAge;

        private MilestoneAgeHolder(View itemView) {
            super(itemView);
            mMilestoneAgeTextView = (TextView) itemView.findViewById(R.id.milestone_age_text_view);
            itemView.setOnClickListener(this);
        }

        private void bindMilestone(MilestoneAgeData ageData) {
            mMilestoneAgeTextView.setText(SystemUtils.getFormattedAge(ageData.getAge()));
            mAge = ageData;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestoneAge(mAge);
            }
        }
    }
}
