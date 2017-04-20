package com.intelliviz.retirementhelper.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.IncomeFragment;

/**
 * Created by edm on 4/12/2017.
 */

public class IncomeSourceAdapter extends RecyclerView.Adapter<IncomeSourceAdapter.IncomeSourceHolder>{
    private Cursor mCursor;
    private IncomeFragment.OnSelectIncomeSourceListener mListener;

    public IncomeSourceAdapter() {

    }

    @Override
    public IncomeSourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.income_source_item_layout, parent, false);
        return new IncomeSourceHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeSourceHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        int idIndex = mCursor.getColumnIndex(RetirementContract.InstitutionEntry._ID);
        if(idIndex != -1) {
            long id = mCursor.getLong(idIndex);
            holder.bindIncomeSource(id);
        }
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

    public void setOnSelectIncomeSourceListener (IncomeFragment.OnSelectIncomeSourceListener listener) {
        mListener = listener;
    }

    public class IncomeSourceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private long mId;

        public IncomeSourceHolder(View itemView) {
            super(itemView);
        }

        public void bindIncomeSource(long id) {
            mId = id;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
