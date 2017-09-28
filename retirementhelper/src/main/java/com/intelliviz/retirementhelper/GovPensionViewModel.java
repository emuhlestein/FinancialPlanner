package com.intelliviz.retirementhelper;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.util.GovPensionDatabase;

/**
 * Created by edm on 9/26/2017.
 */

public class GovPensionViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionIncomeData> mGPID =
            new MutableLiveData<>();
    private GovPensionDatabase mGovPensionDatabase;

    public GovPensionViewModel(Application application, long incomeId) {
        super(application);
        mGovPensionDatabase = GovPensionDatabase.getInstance(this.getApplication());
        new GetGPDIAsyncTask(mGovPensionDatabase).execute(incomeId);
    }

    public LiveData<GovPensionIncomeData> getData() {
        return mGPID;
    }

    public void setData(GovPensionIncomeData gpid) {
        mGPID.setValue(gpid);
        if(gpid.getId() == -1) {
            new InsertGPDIAsyncTask(mGovPensionDatabase).execute(gpid);
        } else {
            new UpdateGPDIAsyncTask(mGovPensionDatabase).execute(gpid);
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;

        public Factory(@NonNull Application application, long incomeId) {
            mApplication = application;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GovPensionViewModel(mApplication, mIncomeId);
        }
    }

    private class GetGPDIAsyncTask extends AsyncTask<Long, Void, GovPensionIncomeData> {
        private GovPensionDatabase mDB;

        public GetGPDIAsyncTask(GovPensionDatabase db) {
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

    private class UpdateGPDIAsyncTask extends AsyncTask<GovPensionIncomeData, Void, Integer> {
        private GovPensionDatabase mDB;

        public UpdateGPDIAsyncTask(GovPensionDatabase db) {
            mDB = db;
        }

        @Override
        protected Integer doInBackground(GovPensionIncomeData... params) {
            return mDB.updateGovPensionData(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertGPDIAsyncTask extends AsyncTask<GovPensionIncomeData, Void, Long> {
        private GovPensionDatabase mDB;

        public InsertGPDIAsyncTask(GovPensionDatabase db) {
            mDB = db;
        }

        @Override
        protected Long doInBackground(GovPensionIncomeData... params) {
            return mDB.insertGovPensionData(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }
}
