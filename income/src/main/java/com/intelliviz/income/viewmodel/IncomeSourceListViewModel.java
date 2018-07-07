package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.repo.IncomeSourceListRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/7/2017.
 */

public class IncomeSourceListViewModel extends AndroidViewModel {
    private IncomeSourceListRepo mIncomeSourceRepo;
    private RetirementOptionsEntityRepo mRetireRepo;
    private LiveData<List<AbstractIncomeSource>> mIncomeSources;
    private LiveData<List<IncomeSourceEntityBase>> mIncomeSourceEntity;


    public IncomeSourceListViewModel(Application application) {
        super(application);
        mIncomeSourceRepo = new IncomeSourceListRepo(application);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        //mIncomeSourceEntity = mIncomeSourceRepo.getIncomeSources();
        subscribe();
    }

    public LiveData<List<AbstractIncomeSource>> get() {
        return mIncomeSources;
    }

    public void subscribe() {
        LiveData<List<IncomeSourceEntityBase>> incomeSourceEntities = mIncomeSourceRepo.getIncomeSources();

        mIncomeSources =
                Transformations.switchMap(incomeSourceEntities,
                        new Function<List<IncomeSourceEntityBase>, LiveData<List<AbstractIncomeSource>>>() {
                            @Override
                            public LiveData<List<AbstractIncomeSource>> apply(List<IncomeSourceEntityBase> input) {
                                return getAllIncomeSources(input);
                            }
                        });
    }

    public void update() {
       mIncomeSourceRepo.update();
    }

    public void delete(AbstractIncomeSource incomeSource) {
        if(incomeSource instanceof GovPension) {
            GovPension source = (GovPension)incomeSource;
            mIncomeSourceRepo.delete(GovPensionEntityMapper.map(source));
        } else if(incomeSource instanceof PensionData) {
            PensionData source = (PensionData)incomeSource;
            mIncomeSourceRepo.delete(PensionDataEntityMapper.map(source));
        } else if(incomeSource instanceof SavingsData) {
            SavingsData source = (SavingsData)incomeSource;
            mIncomeSourceRepo.delete(SavingsDataEntityMapper.map(source));
        }
    }

//    public void updateAppWidget() {
//        new UpdateAppWidgetAsyncTask().execute();
//    }
//
//    public void updateMilestones() {
//        new UpdateSummaryMilestonesAsyncTask().execute();
//    }

   /* private class GetAllIncomeSourcesAsyncTask extends AsyncTask<Void, List<AbstractIncomeSource>, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(Void... params) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteGovPensionAsyncTask extends AsyncTask<GovPension, Void, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(GovPension... params) {
            GovPension gp = params[0];
            MutableLiveData<RetirementOptionsEntity> liveRoe = mRetireRepo.get();
            RetirementOptionsEntity roe = liveRoe.getValue();
            if(gp.isSpouse()) {
                roe.setSpouseBirthdate("0");
                roe.setIncludeSpouse(0);
            } else {
                roe.setBirthdate("0");
            }
            mRetireRepo.update(roe);

            GovPensionEntity gpe = GovPensionEntityMapper.map(params[0]);
            mGovRepo.delete(gpe);
            //mDB.retirementOptionsDao().update(roe);
            //mDB.govPensionDao().delete(params[0]);
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeletePensionAsyncTask extends AsyncTask<PensionData, Void, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(PensionData... params) {
            PensionIncomeEntity pie = PensionDataEntityMapper.map(params[0]);
            mPensionRepo.delete(pie);
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteSavingsAsyncTask extends AsyncTask<SavingsData, Void, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(SavingsData... params) {
            SavingsIncomeEntity sie = SavingsDataEntityMapper.map(params[0]);
            mSavingsRepo.delete(sie);
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }

    private class DeleteTaxDeferredAsyncTask extends AsyncTask<SavingsData, Void, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(SavingsData... params) {
            SavingsIncomeEntity sie = SavingsDataEntityMapper.map(params[0]);
            mSavingsRepo.delete(sie);
            updateAppWidgetSummaryData();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }
*/
    private class UpdateAppWidgetAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            updateAppWidgetSummaryData();
            return null;
        }
    }

   /* private class UpdateSummaryMilestonesAsyncTask extends AsyncTask<Void, Void, List<AbstractIncomeSource>> {

        @Override
        protected List<AbstractIncomeSource> doInBackground(Void... params) {
            updateMilestoneSummary();
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AbstractIncomeSource> incomeSourceEntityBases) {
            mIncomeSources.setValue(incomeSourceEntityBases);
        }
    }*/

    private void updateAppWidgetSummaryData() {
        //AppDatabase db = AppDatabase.getInstance(getApplication());
        //updateSummaryData(db);
        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplication());
        //ComponentName appWidget = new ComponentName(getApplication(), WidgetProvider.class);
        //int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    private void updateSummaryData(AppDatabase db) {
        //db.summaryDao().deleteAll();
        //List<MilestoneData> milestones = null;
        //for(MilestoneData msd : milestones) {
        //    db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        //}
    }

    private void updateMilestoneSummary() {
    }

    private LiveData<List<AbstractIncomeSource>> getAllIncomeSources(List<IncomeSourceEntityBase> list) {
        LiveData<RetirementOptionsEntity> liveRoe = mRetireRepo.get();
        RetirementOptionsEntity roe = liveRoe.getValue();
        if(roe == null) {
            return new MutableLiveData<>();
        }
        AgeData endAge = roe.getEndAge();

        List<AbstractIncomeSource> incomeSourceList = new ArrayList<>();

        List<GovPension> gpList = new ArrayList<>();
        for(IncomeSourceEntityBase entity : list) {
            if(entity instanceof GovPensionEntity) {
                GovPension gp = GovPensionEntityMapper.map((GovPensionEntity)entity);
                gpList.add(gp);
                incomeSourceList.add(gp);
            }

            if(entity instanceof PensionIncomeEntity) {
                PensionData pd = PensionDataEntityMapper.map((PensionIncomeEntity)entity);
                pd.setRules(new PensionRules(roe.getBirthdate(), pd.getAge(), endAge, pd.getBenefit()));
                incomeSourceList.add(pd);
            }

            if(entity instanceof SavingsIncomeEntity) {
                SavingsData savingsData = SavingsDataEntityMapper.map((SavingsIncomeEntity)entity);
                savingsData.setRules(new Savings401kIncomeRules(roe.getBirthdate(), endAge));
                incomeSourceList.add(savingsData);
            }
        }

        SocialSecurityRules.setRulesOnGovPensionEntities(gpList, roe);

        MutableLiveData<List<AbstractIncomeSource>> incomeSources = new MutableLiveData<>();
        incomeSources.setValue(incomeSourceList);

        return incomeSources;
    }
    private class UpdateSpouseBirthdateAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mRetireRepo.get().getValue();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mRetireRepo.update(roe);
            return null;
        }
    }
}
