package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.db.PensionDatabase;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class PensionViewModel extends AndroidViewModel {
    private MutableLiveData<PensionIncomeData> mPID =
            new MutableLiveData<>();
    private PensionDatabase mDB;

    public PensionViewModel(Application application, long incomeId) {
        super(application);
        mDB = PensionDatabase.getInstance(this.getApplication());
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<PensionIncomeData> getData() {
        return mPID;
    }

    public LiveData<List<PensionIncomeData>> getList() {
        return null;
    }

    public void setData(PensionIncomeData pid) {
        mPID.setValue(pid);
        if(pid.getId() == -1) {
            new InsertAsyncTask().execute(pid);
        } else {
            new UpdateAsyncTask().execute(pid);
        }
    }

    public void delete(long id) {
        new DeleteAsyncTask().execute(id);
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
            return (T) new PensionViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, PensionIncomeData> {

        @Override
        protected PensionIncomeData doInBackground(Long... params) {
            return (PensionIncomeData)mDB.get(params[0]);
        }

        @Override
        protected void onPostExecute(PensionIncomeData pid) {
            mPID.setValue(pid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<PensionIncomeData, Void, Integer> {

        @Override
        protected Integer doInBackground(PensionIncomeData... params) {
            return mDB.update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<PensionIncomeData, Void, Long> {

        @Override
        protected Long doInBackground(PensionIncomeData... params) {
            return mDB.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Long, Void, Integer> {

        @Override
        protected Integer doInBackground(Long... params) {
            return mDB.delete(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsInserted) {
        }
    }
}
