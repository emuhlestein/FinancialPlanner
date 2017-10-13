package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/7/2017.
 */

public class IncomeSourceListViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<List<IncomeSourceEntityBase>> mIncomeSources = new MutableLiveData<>();

    public IncomeSourceListViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAllIncomeSourcesAsyncTask().execute();
    }

    public LiveData<List<IncomeSourceEntityBase>> get() {
        return mIncomeSources;
    }

    public void update() {
        new GetAllIncomeSourcesAsyncTask().execute();
    }

    public void delete(IncomeSourceEntityBase incomeSource) {
        if(incomeSource instanceof GovPensionEntity) {
            GovPensionEntity entity = (GovPensionEntity)incomeSource;
            new DeleteGovPensionAsyncTask().execute(entity);
        } else if(incomeSource instanceof PensionIncomeEntity) {
            PensionIncomeEntity entity = (PensionIncomeEntity)incomeSource;
            new DeletePensionAsyncTask().execute(entity);
        } else if(incomeSource instanceof SavingsIncomeEntity) {
            SavingsIncomeEntity entity = (SavingsIncomeEntity)incomeSource;
            new DeleteSavingsAsyncTask().execute(entity);
        } else if(incomeSource instanceof TaxDeferredIncomeEntity) {
            TaxDeferredIncomeEntity entity = (TaxDeferredIncomeEntity)incomeSource;
            new DeleteTaxDeferredAsyncTask().execute(entity);
        }
    }

    private class GetAllIncomeSourcesAsyncTask extends AsyncTask<Void, List<IncomeSourceEntityBase>, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(Void... params) {
            List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
            List<TaxDeferredIncomeEntity> tdieList = mDB.taxDeferredIncomeDao().get();
            for(TaxDeferredIncomeEntity tdie : tdieList) {
                incomeSourceList.add(tdie);
            }
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            for(GovPensionEntity gpie : gpeList) {
                incomeSourceList.add(gpie);
            }
            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            for(PensionIncomeEntity pie : pieList) {
                incomeSourceList.add(pie);
            }
            List<SavingsIncomeEntity> sieList = mDB.savingsIncomeDao().get();
            for(SavingsIncomeEntity sie : sieList) {
                incomeSourceList.add(sie);
            }

            return incomeSourceList;
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteGovPensionAsyncTask extends AsyncTask<GovPensionEntity, Void, Void> {

        @Override
        protected Void doInBackground(GovPensionEntity... params) {
            mDB.govPensionDao().delete(params[0]);
            SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }

    private class DeletePensionAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().delete(params[0]);
            SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }

    private class DeleteSavingsAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }

    private class DeleteTaxDeferredAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(TaxDeferredIncomeEntity... params) {
            mDB.taxDeferredIncomeDao().delete(params[0]);
            SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }
}
