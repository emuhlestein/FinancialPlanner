package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.TaxDeferredDatabase;

/**
 * Created by edm on 9/30/2017.
 */

public class TaxDeferredViewModel extends AndroidViewModel {
    private MutableLiveData<TaxDeferredIncomeData> mTDID =
            new MutableLiveData<>();
    private TaxDeferredDatabase mDB;

    public TaxDeferredViewModel(Application application, long incomeId) {
        super(application);
        mDB = TaxDeferredDatabase.getInstance(this.getApplication());
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<TaxDeferredIncomeData> getData() {
        return mTDID;
    }

    public void setData(TaxDeferredIncomeData tdid) {
        mTDID.setValue(tdid);
        if(tdid.getId() == -1) {
            new InsertAsyncTask().execute(tdid);
        } else {
            new UpdateAsyncTask().execute(tdid);
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
            return (T) new TaxDeferredViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, TaxDeferredIncomeData> {

        public GetAsyncTask() {
        }

        @Override
        protected TaxDeferredIncomeData doInBackground(Long... params) {
            return (TaxDeferredIncomeData)mDB.get(params[0]);
        }

        @Override
        protected void onPostExecute(TaxDeferredIncomeData tdid) {
            mTDID.setValue(tdid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<TaxDeferredIncomeData, Void, Integer> {

        @Override
        protected Integer doInBackground(TaxDeferredIncomeData... params) {
            return mDB.update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<TaxDeferredIncomeData, Void, Long> {

        @Override
        protected Long doInBackground(TaxDeferredIncomeData... params) {
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
