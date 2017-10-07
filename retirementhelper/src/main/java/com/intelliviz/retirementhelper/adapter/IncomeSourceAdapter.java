package com.intelliviz.retirementhelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;

import java.util.List;

/**
 * Adapter for income sources.
 * Created by Ed Muhlestein on 4/12/2017.
 */
public class IncomeSourceAdapter extends RecyclerView.Adapter<IncomeSourceAdapter.IncomeSourceHolder> {
    private List<IncomeSourceEntityBase> mIncomeSources;
    private SelectIncomeSourceListener mListener;
    private String[] mIncomeTypeStrings;

    /**
     * Default constructor.
     */
    public IncomeSourceAdapter(List<IncomeSourceEntityBase> incomeSources) {
        mIncomeSources = incomeSources;
    }

    @Override
    public IncomeSourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mIncomeTypeStrings = parent.getResources().getStringArray(R.array.income_types);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.income_source_card, parent, false);
        return new IncomeSourceHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeSourceHolder holder, int position) {
        IncomeSourceEntityBase incomeSource = mIncomeSources.get(position);
        holder.bindIncomeSource(incomeSource);
    }

    @Override
    public int getItemCount() {
        if(mIncomeSources != null) {
            return mIncomeSources.size();
        } else {
            return 0;
        }
    }

    public void update(List<IncomeSourceEntityBase> incomeSources) {
        if(incomeSources != null) {
            mIncomeSources.clear();
            mIncomeSources.addAll(incomeSources);
            notifyDataSetChanged();
        }
    }

    /**
     * Set listener for selecting income source.
     * @param listener The listener.
     */
    public void setOnSelectIncomeSourceListener (SelectIncomeSourceListener listener) {
        mListener = listener;
    }

    class IncomeSourceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private IncomeSourceEntityBase mIncomeSource;
        private long mId;
        private int mIncomeType;
        private String mIncomeSourceName;
        TextView incomeSourceNameTextView;
        TextView incomeTypeTextView;
        ImageView overflowImage;

        IncomeSourceHolder(View itemView) {
            super(itemView);
            incomeSourceNameTextView = (TextView) itemView.findViewById(R.id.income_source_name_text_view);
            incomeTypeTextView = (TextView) itemView.findViewById(R.id.income_source_type_text_view);
            overflowImage = (ImageView) itemView.findViewById(R.id.overflow_image_view);
            overflowImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void bindIncomeSource(IncomeSourceEntityBase incomeSource) {

            mIncomeSourceName = incomeSource.getName();
            incomeSourceNameTextView.setText(mIncomeSourceName);

            mIncomeType = incomeSource.getType();
            incomeTypeTextView.setText(mIncomeTypeStrings[mIncomeType]);

            mId = incomeSource.getId();

            mIncomeSource = incomeSource;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                if(v instanceof LinearLayout) {
                    mListener.onSelectIncomeSource(mIncomeSource, false);
                } else {
                    mListener.onSelectIncomeSource(mIncomeSource, true);
                }
            }
        }
    }
}
