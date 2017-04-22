package com.intelliviz.retirementhelper.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.IncomeSourceFragment;

/**
 * Created by edm on 4/12/2017.
 */

public class IncomeSourceAdapter extends RecyclerView.Adapter<IncomeSourceAdapter.IncomeSourceHolder>{
    private Cursor mCursor;
    private IncomeSourceFragment.OnSelectIncomeSourceListener mListener;
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

    public void setOnSelectIncomeSourceListener (IncomeSourceFragment.OnSelectIncomeSourceListener listener) {
        mListener = listener;
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
        }

        public void bindIncomeSource() {
            int nameIndex = mCursor.getColumnIndex(RetirementContract.InstitutionEntry.COLUMN_NAME);
            int typeIndex = mCursor.getColumnIndex(RetirementContract.InstitutionEntry.COLUMN_TYPE);
            if(nameIndex != -1) {
                institutionName.setText(mCursor.getString(nameIndex));
            }
            if(typeIndex != -1) {
                incomeType.setText(mIncomeTypes[mCursor.getInt(typeIndex)]);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
