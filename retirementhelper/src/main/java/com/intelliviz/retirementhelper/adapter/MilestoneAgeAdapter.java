package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 6/22/2017.
 */

public class MilestoneAgeAdapter extends RecyclerView.Adapter<MilestoneAgeAdapter.MilestoneAgeHolder> {
    private Cursor mCursor;
    private SelectMilestoneAgeListener mListener;
    private List<MilestoneAgeData> mMilestoneAges;
    private Context mContext;
    private int mIdIndex;

    public MilestoneAgeAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mIdIndex = cursor.getColumnIndex("_id");
        //mMilestoneAges = milestones;
    }

    @Override
    public MilestoneAgeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_age_item, parent, false);
        return new MilestoneAgeHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneAgeHolder holder, int position) {
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

    @Override
    public long getItemId(int position) {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mIdIndex);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    // TODO remove
    public void update(List<MilestoneAgeData> ages) {
        //mMilestoneAges.clear();
        //mMilestoneAges.addAll(ages);
        //notifyDataSetChanged();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
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

        private void bindMilestone(Cursor cursor) {

            int idIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry._ID);
            int ageIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry.COLUMN_AGE);
            long id = cursor.getLong(idIndex);
            String ageString = cursor.getString(ageIndex);
            AgeData age = SystemUtils.parseAgeString(ageString);
            mMilestoneAgeTextView.setText(SystemUtils.getFormattedAge(age));
            mAge = new MilestoneAgeData(id, age);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectMilestoneAge(mAge);
            }
        }
    }
}
