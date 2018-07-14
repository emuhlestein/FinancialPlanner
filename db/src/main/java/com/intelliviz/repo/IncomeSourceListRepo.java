package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.ArrayList;
import java.util.List;

public class IncomeSourceListRepo {
    private GovEntityRepo mGovRepo;
    private PensionIncomeEntityRepo mPensionRepo;
    private SavingsIncomeEntityRepo mSavingsRepo;
    private RetirementOptionsEntityRepo mRetireRepo;
    private MutableLiveData<List<IncomeSourceEntityBase>> mIncomeSources = new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSourceListRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        mGovRepo = new GovEntityRepo(application);
        mPensionRepo = PensionIncomeEntityRepo.getInstance(application);
        mSavingsRepo = SavingsIncomeEntityRepo.getInstance(application, 0);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        new GetAllIncomeSourcesAsyncTask().execute();
    }

    public LiveData<List<IncomeSourceEntityBase>> getIncomeSources() {
        return mIncomeSources;
    }

    public void update() {
        new GetAllIncomeSourcesAsyncTask().execute();
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
        LiveData<List<GovPensionEntity>> gpeList = mGovRepo.getList();
        if(gpeList != null) {
            List<GovPensionEntity> list = gpeList.getValue();
            if(list != null) {
                for (GovPensionEntity gpe : gpeList.getValue()) {
                    incomeSourceList.add(gpe);
                }
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
            GovPensionEntity gpe = params[0];
            RetirementOptionsEntity roe = mRetireRepo.getImmediate();
            if(gpe.getSpouse() == 1) {
                roe.setSpouseBirthdate("0");
                roe.setIncludeSpouse(0);
            } else {
                roe.setBirthdate("0");
            }
            mRetireRepo.update(RetirementOptionsMapper.map(roe));

            mGovRepo.delete(gpe);
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
