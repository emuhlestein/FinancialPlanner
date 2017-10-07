package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class MilestoneSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<MilestoneData>> mMilestones = new MutableLiveData<>();
    private AppDatabase mDB;

    public MilestoneSummaryViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new MilestoneAsyncTask().execute();
    }

    public LiveData<List<MilestoneData>> getList() {
        return mMilestones;
    }

    private class MilestoneAsyncTask extends AsyncTask<Void, Void, List<MilestoneData>> {

        @Override
        protected List<MilestoneData> doInBackground(Void... voids) {
            return DataBaseUtils.getAllMilestones(mDB);
        }

        @Override
        protected void onPostExecute(List<MilestoneData> milestoneDatas) {
            mMilestones.setValue(milestoneDatas);
        }
    }
}
