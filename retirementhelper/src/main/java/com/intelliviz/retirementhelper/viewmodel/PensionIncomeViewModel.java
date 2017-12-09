package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class PensionIncomeViewModel extends AndroidViewModel {
    private MutableLiveData<PensionIncomeEntity> mPID =
            new MutableLiveData<>();
    private AppDatabase mDB;

    public PensionIncomeViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application); //PensionDatabase.getInstance(this.getApplication());
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<PensionIncomeEntity> getData() {
        return mPID;
    }

    public LiveData<List<PensionIncomeEntity>> getList() {
        return null;
    }

    public void setData(PensionIncomeEntity pid) {
        mPID.setValue(pid);
        if(pid.getId() == 0) {
            new InsertAsyncTask().execute(pid);
        } else {
            new UpdateAsyncTask().execute(pid);
        }
    }

    public void delete(PensionIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
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
            return (T) new PensionIncomeViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, PensionIncomeEntity> {

        @Override
        protected PensionIncomeEntity doInBackground(Long... params) {
            return mDB.pensionIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(PensionIncomeEntity pid) {
            mPID.setValue(pid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(PensionIncomeEntity... params) {
            return mDB.pensionIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(PensionIncomeEntity... params) {
            return mDB.pensionIncomeDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().delete(params[0]);
            return null;
        }
    }
}
