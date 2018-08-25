package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.IncomeSourceDataEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.ArrayList;
import java.util.List;

public class IncomeSourceListRepo {
    private MutableLiveData<List<IncomeSourceEntityBase>> mIncomeSources = new MutableLiveData<>();
    private AppDatabase mDB;
    private MutableLiveData<IncomeSourceDataEx> mDataEx = new MutableLiveData<>();

    public IncomeSourceListRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetAllIncomeSourcesAsyncTask().execute();
    }

    public LiveData<IncomeSourceDataEx> getIncomeSourceDataEx() {
        MutableLiveData<IncomeSourceDataEx> incomeSourceDataEx = new MutableLiveData<>();
        mDataEx = incomeSourceDataEx;
        load();
        return mDataEx;
    }

    public LiveData<List<IncomeSourceEntityBase>> getIncomeSources() {
        return mIncomeSources;
    }

    public void update() {
        load();
    }

    public void load() {
        new GetExAsyncTask().execute();
    }

    private class GetExAsyncTask extends AsyncTask<Void, IncomeSourceDataEx, IncomeSourceDataEx> {
        @Override
        protected IncomeSourceDataEx doInBackground(Void... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new IncomeSourceDataEx(getAllIncomeSources(), roe);
        }

        @Override
        protected void onPostExecute(IncomeSourceDataEx incomeSourceDataEx) {
            mDataEx.setValue(incomeSourceDataEx);
        }
    }

    private class GetAllIncomeSourcesAsyncTask extends AsyncTask<Void, List<IncomeSourceEntityBase>, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(Void... params) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private List<IncomeSourceEntityBase> getAllIncomeSources() {
        List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        if (gpeList != null) {
            for (GovPensionEntity gpe : gpeList) {
                incomeSourceList.add(gpe);
            }
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        if (pieList != null) {
            for (PensionIncomeEntity pie : pieList) {
                incomeSourceList.add(pie);
            }
        }

        List<SavingsIncomeEntity> savingsList = mDB.savingsIncomeDao().get();
        if (savingsList != null) {
            for (SavingsIncomeEntity savings : savingsList) {
                incomeSourceList.add(savings);
            }
        }

        return incomeSourceList;
    }

    public void delete(IncomeSourceEntityBase incomeSourceEntity) {
        if(incomeSourceEntity instanceof GovPensionEntity) {
            GovPensionEntity entity = (GovPensionEntity)incomeSourceEntity;
            new DeleteGovPensionAsyncTask().execute(entity);
        } else if(incomeSourceEntity instanceof PensionIncomeEntity) {
            PensionIncomeEntity entity = (PensionIncomeEntity)incomeSourceEntity;
            new DeletePensionAsyncTask().execute(entity);
        } else if(incomeSourceEntity instanceof SavingsIncomeEntity) {
            SavingsIncomeEntity source = (SavingsIncomeEntity)incomeSourceEntity;
            new DeleteSavingsAsyncTask().execute(source);
        }
    }

    private class DeleteGovPensionAsyncTask extends AsyncTask<GovPensionEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(GovPensionEntity... params) {
            mDB.govPensionDao().delete(params[0]);
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeletePensionAsyncTask extends AsyncTask<PensionIncomeEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().delete(params[0]);
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteSavingsAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }
}
