package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.util.SelectIncomeDetailsListener;

import java.util.List;

/**
 * Created by edm on 11/16/2017.
 */

public class IncomeDetailsAdapter extends RecyclerView.Adapter<IncomeDetailsAdapter.GovPensionHolder>{
    private List<IncomeDetails> mIncomeDetails;
    private Context mContext;
    private SelectIncomeDetailsListener mListener;

    public IncomeDetailsAdapter(Context context, List<IncomeDetails> milestones) {
        mContext = context;
        mIncomeDetails = milestones;
    }

    @Override
    public IncomeDetailsAdapter.GovPensionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout, parent, false);
        return new IncomeDetailsAdapter.GovPensionHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeDetailsAdapter.GovPensionHolder holder, int position) {
        IncomeDetails incomeDetails = mIncomeDetails.get(position);
        holder.bindIncomeDetails(incomeDetails);
    }

    @Override
    public int getItemCount() {
        if(mIncomeDetails != null) {
            return mIncomeDetails.size();
        } else {
            return 0;
        }
    }

    public void setSelectIncomeDetailsListener(SelectIncomeDetailsListener listener) {
        mListener = listener;
    }

    public void update(List<IncomeDetails> incomeDetails) {
        if(incomeDetails != null) {
            mIncomeDetails.clear();
            mIncomeDetails.addAll(incomeDetails);
            notifyDataSetChanged();
        }
    }

    class GovPensionHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mLine1TextView;
        private TextView mLine2TextView;
        private LinearLayout mLinearLayout;
        private IncomeDetails mIncomeDetails;

        public GovPensionHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.gov_pension_item_layout);
            mLine1TextView = itemView.findViewById(R.id.line1);
            mLine2TextView = itemView.findViewById(R.id.line2);
            itemView.setOnClickListener(this);
        }

        private void bindIncomeDetails(IncomeDetails incomeDetails) {

            mIncomeDetails = incomeDetails;
/*
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
*/
            mLine1TextView.setText(incomeDetails.getLine1());
            mLine2TextView.setText(incomeDetails.getLine2());
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectIncomeDetails(mIncomeDetails);
            }
        }
    }
}
