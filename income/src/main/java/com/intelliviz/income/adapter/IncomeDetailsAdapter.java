package com.intelliviz.income.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelliviz.income.R;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.income.ui.IncomeDetailsSelectListener;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.List;

/**
 * Created by edm on 11/16/2017.
 */

public class IncomeDetailsAdapter extends RecyclerView.Adapter<IncomeDetailsAdapter.IncomeDetailsHolder>{
    private List<IncomeDetails> mIncomeDetails;
    private Context mContext;
    private int mNumLines;
    private IncomeDetailsSelectListener mListener;

    public IncomeDetailsAdapter(Context context, List<IncomeDetails> incomeDetails) {
        mContext = context;
        mIncomeDetails = incomeDetails;
    }

    @Override
    public IncomeDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(mNumLines == 1) {
            view = inflater.inflate(R.layout.card_layout_1line, parent, false);
        } else {
            view = inflater.inflate(R.layout.card_layout_3line, parent, false);
        }
        return new IncomeDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeDetailsHolder holder, int position) {
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

    public void update(List<IncomeDetails> incomeDetails) {
        if(incomeDetails != null && !incomeDetails.isEmpty()) {
            mNumLines = incomeDetails.get(0).getNumLines();
            mIncomeDetails.clear();
            mIncomeDetails.addAll(incomeDetails);
            notifyDataSetChanged();
        }
    }

    public void setIncomeDetailsSelectListener(IncomeDetailsSelectListener listener) {
        mListener = listener;
    }

    class IncomeDetailsHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private IncomeDetails mIncomeDetails;
        private TextView mLine1TextView;
        private TextView mLine2TextView;
        private TextView mLine3TextView;
        private ImageView mImageView;
        private android.support.v7.widget.CardView mCardView;

        public IncomeDetailsHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_view);
            if(mNumLines == 1) {
                mLine1TextView = itemView.findViewById(R.id.line1);
                
            } else {
                mLine1TextView = itemView.findViewById(R.id.line1);
                mLine2TextView = itemView.findViewById(R.id.line2);
                mLine3TextView = itemView.findViewById(R.id.line3);
            }
            itemView.setOnClickListener(this);
            mImageView = itemView.findViewById(R.id.info_image);
            mImageView.setOnClickListener(this);
        }

        private void bindIncomeDetails(IncomeDetails incomeDetails) {

            mIncomeDetails = incomeDetails;

            int cardColor = mContext.getResources().getColor(R.color.card_green);
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if(isBenefitInfo(incomeDetails, RetirementConstants.BI_EXHAUSTED_BALANCE)) {
                    cardColor = mContext.getResources().getColor(R.color.card_red);
                } else {
                    if(isBenefitInfo(incomeDetails, RetirementConstants.BI_LOW_BALANCE)) {
                        cardColor = mContext.getResources().getColor(R.color.card_yellow);
                    } else if(isBenefitInfo(incomeDetails, RetirementConstants.BI_GOOD)){
                        cardColor = mContext.getResources().getColor(R.color.card_green);
                    }
                }
            } else {
                if(isBenefitInfo(incomeDetails, RetirementConstants.BI_EXHAUSTED_BALANCE)) {
                    cardColor = mContext.getResources().getColor(R.color.card_red);
                } else {
                    if(isBenefitInfo(incomeDetails, RetirementConstants.BI_LOW_BALANCE)) {
                        cardColor = mContext.getResources().getColor(R.color.card_yellow);
                    } else if(isBenefitInfo(incomeDetails, RetirementConstants.BI_GOOD)) {
                        cardColor = mContext.getResources().getColor(R.color.card_green);
                    }
                }
            }

            if(isBenefitInfo(incomeDetails, RetirementConstants.BI_PENALTY)) {
                cardColor = mContext.getResources().getColor(R.color.card_red);
            }

            mCardView.setCardBackgroundColor(cardColor);

            if(incomeDetails.getNumLines() == 1) {
                if(incomeDetails.hasDetails()) {
                    mImageView.setImageResource(R.drawable.blue_info_icon_36);
                } else {
                    mImageView.setImageResource(0);
                }
                mLine1TextView.setText(incomeDetails.getLine1());
                //mLine1TextView.setText("Line 1\nLine 2\nLine 3\nLine 4");
            } else {
                mLine1TextView.setText(incomeDetails.getLine1());
                mLine2TextView.setText(incomeDetails.getLine2());
                mLine3TextView.setText(incomeDetails.getLine3());
            }
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                if(mIncomeDetails.isClickAccepted()) {
                    mListener.onIncomeDetailsSelect(mIncomeDetails);
                }
            }
        }

        private boolean isBenefitInfo(IncomeDetails incomeDetails, int flag) {
            if((incomeDetails.getBenefitInfo() & flag) == flag) {
                return true;
            } else {
                return false;
            }
        }
    }
}
