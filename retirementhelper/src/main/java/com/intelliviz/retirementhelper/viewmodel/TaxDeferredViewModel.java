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
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;

/**
 * Created by edm on 9/30/2017.
 */

public class TaxDeferredViewModel extends AndroidViewModel {
    private MutableLiveData<TaxDeferredIncomeEntity> mTDID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public TaxDeferredViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<TaxDeferredIncomeEntity> getData() {
        return mTDID;
    }

    public void setData(TaxDeferredIncomeEntity tdid) {
        mTDID.setValue(tdid);
        if(tdid.getId() == 0) {
            new InsertAsyncTask().execute(tdid);
        } else {
            new UpdateAsyncTask().execute(tdid);
        }
    }

    public void delete(TaxDeferredIncomeEntity entity) {
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
            return (T) new TaxDeferredViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, TaxDeferredIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected TaxDeferredIncomeEntity doInBackground(Long... params) {
            return mDB.taxDeferredIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(TaxDeferredIncomeEntity tdid) {
            mTDID.setValue(tdid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(TaxDeferredIncomeEntity... params) {
            return mDB.taxDeferredIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(TaxDeferredIncomeEntity... params) {
            return mDB.taxDeferredIncomeDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            mId = id;
        }
    }

    private class DeleteAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(TaxDeferredIncomeEntity... params) {
            mDB.taxDeferredIncomeDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Integer numRowsInserted) {
        }
    }
}
