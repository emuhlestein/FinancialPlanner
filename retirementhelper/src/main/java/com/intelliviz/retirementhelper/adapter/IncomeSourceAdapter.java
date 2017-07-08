package com.intelliviz.retirementhelper.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;

/**
 * Adapter for income sources.
 * Created by Ed Muhlestein on 4/12/2017.
 */
public class IncomeSourceAdapter extends RecyclerView.Adapter<IncomeSourceAdapter.IncomeSourceHolder> {
    private Cursor mCursor;
    private SelectIncomeSourceListener mListener;
    private String[] mIncomeTypes;

    /**
     * Default constructor.
     */
    public IncomeSourceAdapter() {

    }

    @Override
    public IncomeSourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mIncomeTypes = parent.getResources().getStringArray(R.array.income_types);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.income_source_card, parent, false);
        return new IncomeSourceHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeSourceHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        holder.bindIncomeSource(mCursor);
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
     * Set listener for selecting income source.
     * @param listener The listener.
     */
    public void setOnSelectIncomeSourceListener (SelectIncomeSourceListener listener) {
        mListener = listener;
    }

    class IncomeSourceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
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

        void bindIncomeSource(Cursor cursor) {
            int idIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
            int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
            int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
            if(nameIndex != -1) {
                mIncomeSourceName = cursor.getString(nameIndex);
                incomeSourceNameTextView.setText(mIncomeSourceName);
            }
            if(typeIndex != -1) {
                mIncomeType = cursor.getInt(typeIndex);
                incomeTypeTextView.setText(mIncomeTypes[mIncomeType]);
            }
            if(idIndex != -1) {
                String id = cursor.getString(idIndex);
                mId = Long.parseLong(id);
            }
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                if(v instanceof LinearLayout) {
                    mListener.onSelectIncomeSource(mId, mIncomeType, false);
                } else {
                    mListener.onSelectIncomeSource(mId, mIncomeType, true);
                }
            }
        }
    }
}
