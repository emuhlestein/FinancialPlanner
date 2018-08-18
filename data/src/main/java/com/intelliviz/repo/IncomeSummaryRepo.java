package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.IncomeSummaryEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.ArrayList;
import java.util.List;

public class IncomeSummaryRepo {
    private MutableLiveData<IncomeSummaryEx> mIncomeSummaryEx =
            new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSummaryRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetAllIncomeSummariesAsyncTask().execute();
    }

    public LiveData<IncomeSummaryEx> get() {
        return mIncomeSummaryEx;
    }

    public void update() {
        new GetAllIncomeSummariesAsyncTask().execute();
    }

    private class GetAllIncomeSummariesAsyncTask extends AsyncTask<Void, Void, IncomeSummaryEx> {

        @Override
        protected IncomeSummaryEx doInBackground(Void... params) {

            List<IncomeSourceEntityBase> listIncomeSources = getAllIncomeSources();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new IncomeSummaryEx(roe, listIncomeSources);
        }

        @Override
        protected void onPostExecute(IncomeSummaryEx incomeSource) {
            mIncomeSummaryEx.setValue(incomeSource);
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
}
