package com.intelliviz.retirementhelper.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import java.util.List;

/**
 * Created by edm on 8/5/2017.
 */

public class MilestoneSyncTask extends AsyncTask<Void, Void, List<MilestoneData>> {
    private MilestoneLoadListener mListener;
    private Context mContext;

    interface MilestoneLoadListener {
        void onLoadMilestones(List<MilestoneData> milestoneData);
    }

    public MilestoneSyncTask(Context context, MilestoneLoadListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected List<MilestoneData> doInBackground(Void... params) {
        RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(mContext);
        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(mContext, rod);
        List<MilestoneData> milestoneData = DataBaseUtils.getAllMilestones(mContext, ages, rod);
        return milestoneData;
    }

    @Override
    protected void onPostExecute(List<MilestoneData> milestoneData) {
        if(mListener != null) {
            mListener.onLoadMilestones(milestoneData);
        }
    }
}
