package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class SavingsViewModel  extends AndroidViewModel {
    private MutableLiveData<SavingsIncomeEntity> mSID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private LiveData<List<MilestoneData>> mMilestones;

    public SavingsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<SavingsIncomeEntity> getData() {
        return mSID;
    }

    public LiveData<List<SavingsIncomeEntity>> getList() {
        return null;
    }

    public void setData(SavingsIncomeEntity sid) {
        mSID.setValue(sid);
        if(sid.getId() == 0) {
            new InsertAsyncTask().execute(sid);
        } else {
            new UpdateAsyncTask().execute(sid);
        }
    }

    public void delete(SavingsIncomeEntity entity) {
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
            return (T) new SavingsViewModel(mApplication, mIncomeId);
        }
    }


    private class InsertAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }


    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeEntity> {

        @Override
        protected SavingsIncomeEntity doInBackground(Long... params) {
            return mDB.savingsIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(SavingsIncomeEntity sid) {
            mSID.setValue(sid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            return null;
        }
    }
}
