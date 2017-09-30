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
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.SavingsDatabase;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class SavingsViewModel  extends AndroidViewModel {
    private MutableLiveData<SavingsIncomeData> mSID =
            new MutableLiveData<>();
    private SavingsDatabase mSavingsDatabase;
    private LiveData<List<MilestoneData>> mMilestones;

    public SavingsViewModel(Application application, long incomeId) {
        super(application);
        mSavingsDatabase = mSavingsDatabase.getInstance(this.getApplication());
        new GetAsyncTask(mSavingsDatabase).execute(incomeId);
    }

    public LiveData<SavingsIncomeData> getData() {
        return mSID;
    }

    public LiveData<List<SavingsIncomeData>> getList() {
        return null;
    }

    public void setData(SavingsIncomeData sid) {
        mSID.setValue(sid);
        if(sid.getId() == -1) {
            new InsertAsyncTask(mSavingsDatabase).execute(sid);
        } else {
            new UpdateAsyncTask(mSavingsDatabase).execute(sid);
        }
    }

    public void delete(long id) {
        new DeleteAsyncTask(mSavingsDatabase).execute(id);
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


    private class InsertAsyncTask extends AsyncTask<SavingsIncomeData, Void, Long> {
        private SavingsDatabase mDB;

        public InsertAsyncTask(SavingsDatabase db) {
            mDB = db;
        }

        @Override
        protected Long doInBackground(SavingsIncomeData... params) {
            return mDB.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }


    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeData> {
        private SavingsDatabase mDB;

        public GetAsyncTask(SavingsDatabase db) {
            mDB = db;
        }

        @Override
        protected SavingsIncomeData doInBackground(Long... params) {
            return (SavingsIncomeData)mDB.get(params[0]);
        }

        @Override
        protected void onPostExecute(SavingsIncomeData sid) {
            mSID.setValue(sid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeData, Void, Integer> {
        private SavingsDatabase mDB;

        public UpdateAsyncTask(SavingsDatabase db) {
            mDB = db;
        }

        @Override
        protected Integer doInBackground(SavingsIncomeData... params) {
            return mDB.update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Long, Void, Integer> {
        private SavingsDatabase mDB;

        public DeleteAsyncTask(SavingsDatabase db) {
            mDB = db;
        }

        @Override
        protected Integer doInBackground(Long... params) {
            return mDB.delete(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsInserted) {
        }
    }
}
