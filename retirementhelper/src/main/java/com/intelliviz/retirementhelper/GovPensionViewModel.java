package com.intelliviz.retirementhelper;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.util.GovPensionDatabase;

/**
 * Created by edm on 9/26/2017.
 */

public class GovPensionViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionIncomeData> mGPID =
            new MutableLiveData<>();
    private GovPensionDatabase mGovPensionDatabase;

    public GovPensionViewModel(Application application) {
        super(application);
        mGovPensionDatabase = GovPensionDatabase.getInstance(this.getApplication());
        //new GPDIAsyncTask(mGovPensionDatabase).execute(id);
    }

    public LiveData<GovPensionIncomeData> getData(long id) {
        if(id != -1) {
            new GPDIAsyncTask(mGovPensionDatabase).execute(id);
        }
        return mGPID;
    }

    private class GPDIAsyncTask extends AsyncTask<Long, Void, GovPensionIncomeData> {
        private GovPensionDatabase mDB;

        public GPDIAsyncTask(GovPensionDatabase db) {
            mDB = db;
        }

        @Override
        protected GovPensionIncomeData doInBackground(Long... params) {
            return mDB.getGovPensionIncomeData(params[0]);
        }

        @Override
        protected void onPostExecute(GovPensionIncomeData gpid) {
            mGPID.setValue(gpid);
        }
    }
}
