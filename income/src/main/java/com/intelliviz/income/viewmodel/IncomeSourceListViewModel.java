package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeSourceDataEx;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.income.data.IncomeSourceViewData;
import com.intelliviz.repo.IncomeSourceListRepo;

/**
 * Created by edm on 10/7/2017.
 */

public class IncomeSourceListViewModel extends AndroidViewModel {
    private IncomeSourceListRepo mIncomeSourceRepo;
    private LiveData<IncomeSourceViewData> mViewData;
    private LiveData<IncomeSourceDataEx> mSource;

    public IncomeSourceListViewModel(Application application) {
        super(application);
        mIncomeSourceRepo = new IncomeSourceListRepo(application);
        mSource = mIncomeSourceRepo.getIncomeSourceDataEx();
        subscribe();
    }

    public LiveData<IncomeSourceViewData> get() {
        return mViewData;
    }

    public void subscribe() {
        mViewData =
                Transformations.switchMap(mSource,
                        new Function<IncomeSourceDataEx, LiveData<IncomeSourceViewData>>() {
                            @Override
                            public LiveData<IncomeSourceViewData> apply(IncomeSourceDataEx input) {
                                RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());
                                IncomeSourceHelper helper = new IncomeSourceHelper(input.getList(), ro);
                                return helper.get();
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

    private class UpdateAppWidgetAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            updateAppWidgetSummaryData();
            return null;
        }
    }

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
        //    db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getActualMonthlyBenefit())));
        //}
    }

    private void updateMilestoneSummary() {
    }
}
