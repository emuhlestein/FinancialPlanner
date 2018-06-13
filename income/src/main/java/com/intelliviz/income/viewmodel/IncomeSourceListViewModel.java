package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.Savings401kIncomeRules;
import com.intelliviz.income.data.SocialSecurityRules;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.db.entity.IncomeSourceEntityBase;
import com.intelliviz.income.db.entity.PensionIncomeEntity;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.db.entity.SavingsIncomeEntity;

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

    public void updateSpouseBirthdate(String birthdate) {
        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
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
        } else if(incomeSource instanceof SavingsIncomeEntity) {
            SavingsIncomeEntity entity = (SavingsIncomeEntity)incomeSource;
            new DeleteTaxDeferredAsyncTask().execute(entity);
        }
    }

    public void updateAppWidget() {
        new UpdateAppWidgetAsyncTask().execute();
    }

    public void updateMilestones() {
        new UpdateSummaryMilestonesAsyncTask().execute();
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

    private class DeleteGovPensionAsyncTask extends AsyncTask<GovPensionEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(GovPensionEntity... params) {
            GovPensionEntity gpe = params[0];
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            if(gpe.getSpouse() == 1) {
                roe.setSpouseBirthdate("0");
                roe.setIncludeSpouse(0);
            } else {
                roe.setBirthdate("0");
            }
            mDB.retirementOptionsDao().update(roe);
            mDB.govPensionDao().delete(params[0]);
            updateAppWidgetSummaryData();
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
            updateAppWidgetSummaryData();
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
            //updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteTaxDeferredAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class UpdateAppWidgetAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            updateAppWidgetSummaryData();
            return null;
        }
    }

    private class UpdateSummaryMilestonesAsyncTask extends AsyncTask<Void, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(Void... params) {
            updateMilestoneSummary();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private void updateAppWidgetSummaryData() {
        AppDatabase db = AppDatabase.getInstance(getApplication());
        updateSummaryData(db);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplication());
        //ComponentName appWidget = new ComponentName(getApplication(), WidgetProvider.class);
        //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    private void updateSummaryData(AppDatabase db) {
        db.summaryDao().deleteAll();
        //List<MilestoneData> milestones = null;
        //for(MilestoneData msd : milestones) {
        //    db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        //}
    }

    private void updateMilestoneSummary() {
    }

    private List<IncomeSourceEntityBase> getAllIncomeSources() {
        List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        AgeData endAge = roe.getEndAge();
        for(SavingsIncomeEntity tdie : tdieList) {
            AgeData startAge = tdie.getStartAge();
            Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), endAge);
            tdie.setRules(tdir);
            incomeSourceList.add(tdie);
        }
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for(GovPensionEntity gpie : gpeList) {
            incomeSourceList.add(gpie);
        }
        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for(PensionIncomeEntity pie : pieList) {
            incomeSourceList.add(pie);
        }

        return incomeSourceList;
    }

    private class UpdateSpouseBirthdateAsyncTask extends android.os.AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return null;
        }
    }
}
