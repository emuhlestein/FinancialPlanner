package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Milestone summary adapter.
 * Created by Ed Muhlestein on 5/29/2017.
 */

public class SummaryMilestoneAdapter extends RecyclerView.Adapter<SummaryMilestoneAdapter.MilestoneHolder> {
    private Context mContext;
    private Cursor mCursor;
    private SelectionMilestoneListener mListener;

    public interface SelectionMilestoneListener {
        void onSelectMilestone(Cursor cursor);
    }

    public SummaryMilestoneAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MilestoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_item_layout, parent, false);
        return new MilestoneHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }
        holder.bindMilestone(mCursor);
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * Update the cursor.
     * @param cursor The new cursor.
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Set the listerner for milestones selection.
     * @param listener THe listener.
     */
    public void setOnSelectionMilestoneListener (SelectionMilestoneListener listener) {
        mListener = listener;
    }

    class MilestoneHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private LinearLayout mLinearLayout;

        private MilestoneHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.milestone_item_layout);
            mMilestoneTextView = (TextView) itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = (TextView) itemView.findViewById(R.id.monthly_amount_text_view);
            itemView.setOnClickListener(this);
        }

        private void bindMilestone(Cursor cursor) {
            int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.MilestoneSummaryEntry.COLUMN_MONTHLY_BENEFIT);
            int endBalanceIndex = cursor.getColumnIndex(RetirementContract.MilestoneSummaryEntry.COLUMN_END_BALANCE);
            int penaltyIndex = cursor.getColumnIndex(RetirementContract.MilestoneSummaryEntry.COLUMN_PENALTY_AMOUNT);
            int startAgeIndex = cursor.getColumnIndex(RetirementContract.MilestoneSummaryEntry.COLUMN_START_AGE);
            if(monthlyBenefitIndex == -1 || endBalanceIndex == -1 || penaltyIndex== -1 || startAgeIndex == -1) {
                return;
            }

            String value = cursor.getString(monthlyBenefitIndex);
            double monthlyBenefit = Double.parseDouble(value);

            value = cursor.getString(endBalanceIndex);
            double endBalance = Double.parseDouble(value);

            value = cursor.getString(penaltyIndex);
            double penalty = Double.parseDouble(value);

            value = cursor.getString(startAgeIndex);
            AgeData startAge = SystemUtils.parseAgeString(value);

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

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestone(mCursor);
            }
        }
    }
}
