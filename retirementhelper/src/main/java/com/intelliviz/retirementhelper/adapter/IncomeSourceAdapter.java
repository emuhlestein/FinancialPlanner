package com.intelliviz.retirementhelper.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;

/**
 * Created by edm on 4/12/2017.
 */

public class IncomeSourceAdapter extends RecyclerView.Adapter<IncomeSourceAdapter.IncomeSourceHolder>
        implements RecyclerView.OnItemTouchListener{
    private Cursor mCursor;
    private SelectIncomeSourceListener mListener;
    private String[] mIncomeTypes;

    public IncomeSourceAdapter() {

    }

    @Override
    public IncomeSourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mIncomeTypes = parent.getResources().getStringArray(R.array.income_types);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.income_source_item_layout, parent, false);
        return new IncomeSourceHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeSourceHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        holder.bindIncomeSource();
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public void setOnSelectIncomeSourceListener (SelectIncomeSourceListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public class IncomeSourceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private long mId;
        TextView institutionName;
        TextView incomeType;

        public IncomeSourceHolder(View itemView) {
            super(itemView);
            institutionName = (TextView) itemView.findViewById(R.id.institution_name_text_view);
            incomeType = (TextView) itemView.findViewById(R.id.income_type_text_view);
            incomeType.setOnClickListener(this);
            institutionName.setOnClickListener(this);
        }

        public void bindIncomeSource() {
            int idIndex = mCursor.getColumnIndex(RetirementContract.IncomeSourceEntry._ID);
            int nameIndex = mCursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_NAME);
            int typeIndex = mCursor.getColumnIndex(RetirementContract.IncomeSourceEntry.COLUMN_TYPE);
            if(nameIndex != -1) {
                institutionName.setText(mCursor.getString(nameIndex));
            }
            if(typeIndex != -1) {
                incomeType.setText(mIncomeTypes[mCursor.getInt(typeIndex)]);
            }
            if(idIndex != -1) {
                String id = mCursor.getString(idIndex);
                mId = Long.parseLong(id);
            }
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectIncomeSource(mId);
            }
        }
    }
}
