package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.List;

public class SavingsIncomeEntityRepo {
    private volatile static SavingsIncomeEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<SavingsIncomeEntity>> mSieList = new MutableLiveData<List<SavingsIncomeEntity>>();

    public static SavingsIncomeEntityRepo getInstance(Application application) {
        if(mINSTANCE == null) {
            synchronized (SavingsIncomeEntityRepo.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = new SavingsIncomeEntityRepo(application);
                }
            }
        }
        return mINSTANCE;
    }

    private SavingsIncomeEntityRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetListAsyncTask().execute();
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public MutableLiveData<SavingsIncomeEntity> get(long id) {
        new GetAsyncTask().execute(id);
        return mSIE;
    }

    public MutableLiveData<List<SavingsIncomeEntity>> getList() {
        return mSieList;
    }

    public void setData(SavingsIncomeEntity sie) {
        if(sie.getId() == 0) {
            new InsertAsyncTask().execute(sie);
        } else {
            new UpdateAsyncTask().execute(sie);
        }
    }

    public void delete(SavingsIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
    }

    public void update() {
        new GetListAsyncTask().execute();
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeEntity> {

        GetAsyncTask() {
        }

        @Override
        protected SavingsIncomeEntity doInBackground(Long... params) {
            return mDB.savingsIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(SavingsIncomeEntity tdid) {
            mSIE.setValue(tdid);
        }
    }

    private class GetListAsyncTask extends AsyncTask<Void, Void, List<SavingsIncomeEntity>> {

        @Override
        protected List<SavingsIncomeEntity> doInBackground(Void... params) {
           return mDB.savingsIncomeDao().get();
        }

        @Override
        protected void onPostExecute(List<SavingsIncomeEntity> sie) {
            mSieList.setValue(sie);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            SavingsIncomeEntity sie = params[0];
            return mDB.savingsIncomeDao().update(sie);
        }
    }

    private class InsertAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().insert(params[0]);
        }
    }


    private class DeleteAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Integer numRowsInserted) {
        }
    }
}
