package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class SSMilestoneAdapter extends RecyclerView.Adapter<SSMilestoneAdapter.SSMilestoneHolder> {
    private List<MilestoneData> mMilestones;
    private SelectionMilestoneListener mListener;
    private Context mContext;
    private AgeData mFullAge;
    private AgeData mMinimumAge;

    /**
     * Constructor.
     * @param context The context.
     * @param milestones The list of milestones.
     * @param rod The retirement options data.
     */
    public SSMilestoneAdapter(Context context, List<MilestoneData> milestones, RetirementOptionsData rod) {
        mContext = context;
        mMilestones = milestones;
        String birthdate = rod.getBirthdate();
        int year = SystemUtils.getBirthYear(birthdate);
        mFullAge = GovPensionHelper.getFullRetirementAge(year);

        // TODO refactor
        mMinimumAge = new AgeData(62, 0);
    }

    @Override
    public SSMilestoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_item_layout, parent, false);
        return new SSMilestoneHolder(view);
    }

    @Override
    public void onBindViewHolder(SSMilestoneHolder holder, int position) {
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

    /**
     * Update the milestones.
     * @param milestones The milestoens.
     */
    public void update(List<MilestoneData> milestones) {
        mMilestones.clear();
        mMilestones.addAll(milestones);
        notifyDataSetChanged();
    }

    /**
     * Set the listerner for milestones selection.
     * @param listener THe listener.
     */
    public void setOnSelectionMilestoneListener (SelectionMilestoneListener listener) {
        mListener = listener;
    }

    class SSMilestoneHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private LinearLayout mLinearLayout;
        private MilestoneData mMSD;

        private SSMilestoneHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.milestone_item_layout);
            mMilestoneTextView = (TextView) itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = (TextView) itemView.findViewById(R.id.monthly_amount_text_view);
            itemView.setOnClickListener(this);

        }

        private void bindMilestone(int position) {
            mMSD = mMilestones.get(position);
            AgeData startAge = mMSD.getStartAge();

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {

                if(startAge.isBefore(mMinimumAge)) {
                    mLinearLayout.setBackground( mContext.getResources().getDrawable(R.drawable.red_ripple_effect) );
                } else if(startAge.isBefore(mFullAge)) {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                    } else {
                        mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                    }
            } else {
                if(startAge.isBefore(mMinimumAge)) {
                    mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.red_ripple_effect));
                } else if(startAge.isBefore(mFullAge)) {
                    mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_ripple_effect));
                } else {
                    mLinearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.green_ripple_effect));
                }
            }

            double monthlyAmount = mMSD.getMonthlyBenefit();
            String formattedCurrency = SystemUtils.getFormattedCurrency(monthlyAmount);
            mMonthlyAmountTextView.setText(formattedCurrency);
            mMilestoneTextView.setText(SystemUtils.getFormattedAge(mMSD.getStartAge()));
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestone(mMSD);
            }
        }
    }
}
