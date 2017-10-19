package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.MilestoneSummaryEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.widget.WidgetProvider;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getAllMilestones;

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
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeSourceEntityBase> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteTaxDeferredAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, List<IncomeSourceEntityBase>> {

        @Override
        protected List<IncomeSourceEntityBase> doInBackground(TaxDeferredIncomeEntity... params) {
            mDB.taxDeferredIncomeDao().delete(params[0]);
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
        ComponentName appWidget = new ComponentName(getApplication(), WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    private void updateSummaryData(AppDatabase db) {
        db.summaryDao().deleteAll();
        List<MilestoneData> milestones = getAllMilestones(db);
        for(MilestoneData msd : milestones) {
            db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
    }


    private void updateMilestoneSummary() {

        mDB.milestoneSummaryDao().deleteAll();

        List<MilestoneData> milestoneDataList = DataBaseUtils.getAllMilestones(mDB);

        for (MilestoneData milestoneData : milestoneDataList) {
            String monthlyBenefit = Double.toString(milestoneData.getMonthlyBenefit());
            String startAge = milestoneData.getStartAge().getUnformattedString();
            String endAge = milestoneData.getEndAge().getUnformattedString();
            String minAge = milestoneData.getMinimumAge().getUnformattedString();
            String startBalance = Double.toString(milestoneData.getStartBalance());
            String endBalance = Double.toString(milestoneData.getEndBalance());
            String penaltyAmount = Double.toString(milestoneData.getPenaltyAmount());
            int numMonths = milestoneData.getMonthsFundsFillLast();
            MilestoneSummaryEntity ent = new MilestoneSummaryEntity(0, monthlyBenefit, startAge, endAge, minAge, startBalance, endBalance, penaltyAmount, numMonths);
            mDB.milestoneSummaryDao().insert(ent);
        }
    }

    private List<IncomeSourceEntityBase> getAllIncomeSources() {
        List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
        List<TaxDeferredIncomeEntity> tdieList = mDB.taxDeferredIncomeDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        AgeData endAge = SystemUtils.parseAgeString(roe.getEndAge());
        for(TaxDeferredIncomeEntity tdie : tdieList) {
            TaxDeferredIncomeRules tdir = new TaxDeferredIncomeRules(roe.getBirthdate(), endAge, Double.parseDouble(tdie.getBalance()),
                    Double.parseDouble(tdie.getInterest()), Double.parseDouble(tdie.getMonthlyIncrease()), roe.getWithdrawMode(),
                    Double.parseDouble(roe.getWithdrawAmount()));
            tdie.setRules(tdir);
            incomeSourceList.add(tdie);
        }
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        for(GovPensionEntity gpie : gpeList) {

            String birthdate = roe.getBirthdate();
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, Double.parseDouble(gpie.getFullMonthlyBenefit()));
            gpie.setRules(ssr);
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

}
