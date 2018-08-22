package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.intelliviz.data.IncomeSummaryEx;
import com.intelliviz.data.SavingsDataEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.ArrayList;
import java.util.List;

public class SavingsIncomeEntityRepo {
    private volatile static SavingsIncomeEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<IncomeSummaryEx> mSieList = new MutableLiveData<>();
    private MutableLiveData<SavingsDataEx> mSdEx = new MutableLiveData<>();

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

    public void load(long id) {
        new GetExAsyncTask().execute(id);
    }

    public LiveData<SavingsDataEx> getEx() {
        return mSdEx;
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public MutableLiveData<SavingsIncomeEntity> get(long id) {
        return mSIE;
    }

    public MutableLiveData<IncomeSummaryEx> getList() {
        return mSieList;
    }

    public void setData(SavingsIncomeEntity sie) {
        if(sie.getId() == 0) {
            new InsertAsyncTask().execute(sie);
        } else {
            new UpdateAsyncTask().execute(sie);
        }
    }

    public MutableLiveData<SavingsDataEx> getSavingsDataEx(long id) {
        MutableLiveData<SavingsDataEx> savingsDataEx = new MutableLiveData<>();
        mSdEx = savingsDataEx;
        load(id);
        return savingsDataEx;
    }

    public void delete(SavingsIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
    }

    public void update() {
        new GetListAsyncTask().execute();
    }

    private class GetListAsyncTask extends AsyncTask<Void, Void, IncomeSummaryEx> {

        @Override
        protected IncomeSummaryEx doInBackground(Void... params) {
            List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
            List<SavingsIncomeEntity> list = mDB.savingsIncomeDao().get();
            for(SavingsIncomeEntity sie : list) {
                incomeSourceList.add(sie);
            }
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new IncomeSummaryEx(roe, incomeSourceList);
        }

        @Override
        protected void onPostExecute(IncomeSummaryEx incomeSummaryEx) {
            mSieList.setValue(incomeSummaryEx);
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

    private class GetExAsyncTask extends AsyncTask<Long, Void, SavingsDataEx> {

        public GetExAsyncTask() {
            Log.d("TAG", "HERE B");
        }

        @Override
        protected SavingsDataEx doInBackground(Long... params) {
            SavingsIncomeEntity sie = mDB.savingsIncomeDao().get(params[0]);
            List<SavingsIncomeEntity> sieList = mDB.savingsIncomeDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new SavingsDataEx(sie, sieList.size(), roe);
        }

        @Override
        protected void onPostExecute(SavingsDataEx sdEx) {
            mSdEx.setValue(sdEx);
        }
    }
}
