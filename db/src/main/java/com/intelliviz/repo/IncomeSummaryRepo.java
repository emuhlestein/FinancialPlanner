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
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.ArrayList;
import java.util.List;

public class IncomeSummaryRepo {
    private GovEntityRepo mGovRepo;
    private PensionIncomeEntityRepo mPensionRepo;
    private SavingsIncomeEntityRepo mSavingsRepo;
    private RetirementOptionsEntityRepo mRetireRepo;
    private LiveData<RetirementOptionsEntity> mROE;
    private MutableLiveData<List<IncomeSourceEntityBase>> mIncomeList =
            new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSummaryRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        mRetireRepo = new RetirementOptionsEntityRepo(application);
        mROE = mRetireRepo.get();
        mGovRepo = new GovEntityRepo(application, 0);
        mSavingsRepo = new SavingsIncomeEntityRepo(application, 0);
        mPensionRepo = new PensionIncomeEntityRepo(application);
        new GetAllIncomeSummariesAsyncTask().execute();
    }

    public LiveData<RetirementOptionsEntity> getRetireOptionsEntity() {
        return mROE;
    }

    public LiveData<List<IncomeSourceEntityBase>> get() {
        return mIncomeList;
    }

    private class GetAllIncomeSummariesAsyncTask extends AsyncTask<Void, List<IncomeSourceEntityBase>, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(Void... params) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeList.setValue(incomeSourceEntityBases);
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

        MutableLiveData<List<SavingsIncomeEntity>> savingsList = mSavingsRepo.getList();
        if(savingsList != null) {
            List<SavingsIncomeEntity> slist = savingsList.getValue();
            if(slist != null) {
                for (SavingsIncomeEntity savings : savingsList.getValue()) {
                    incomeSourceList.add(savings);
                }
            }
        }

        return incomeSourceList;
    }
}
