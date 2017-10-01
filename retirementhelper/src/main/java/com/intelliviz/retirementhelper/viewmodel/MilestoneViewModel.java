package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

/**
 * Created by edm on 9/30/2017.
 */

public class MilestoneViewModel extends AndroidViewModel {
    public MilestoneViewModel(Application application) {
        super(application);
    }

/*
    private class MilestoneAsyncTask extends AsyncTask<Long, Void, GovPensionIncomeData> {

        @Override
        protected GovPensionIncomeData doInBackground(Long... params) {
            return (GovPensionIncomeData)mDB.get(params[0]);
        }

        @Override
        protected void onPostExecute(GovPensionIncomeData gpid) {
            mGPID.setValue(gpid);
        }
    }
    */
}
