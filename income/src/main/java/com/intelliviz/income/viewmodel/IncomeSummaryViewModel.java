package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeSummaryHelper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.repo.GovEntityRepo;
import com.intelliviz.repo.IncomeSummaryRepo;
import com.intelliviz.repo.PensionIncomeEntityRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;
import com.intelliviz.repo.SavingsIncomeEntityRepo;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<IncomeData>> mAmountData = new MutableLiveData<>();
    private GovEntityRepo mGovRepo;
    private PensionIncomeEntityRepo mPensionRepo;
    private SavingsIncomeEntityRepo mSavingsRepo;
    private RetirementOptionsEntityRepo mRetireRepo;
    private LiveData<RetirementOptionsEntity> mROE;
    private LiveData<List<IncomeData>> mIncomeSources = new MutableLiveData<>();
    private IncomeSummaryRepo mIncomeRepo;

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mRetireRepo = new RetirementOptionsEntityRepo(application);
        mROE = mRetireRepo.get();
        mGovRepo = new GovEntityRepo(application, 0);
        mSavingsRepo = new SavingsIncomeEntityRepo(application, 0);
        mPensionRepo = new PensionIncomeEntityRepo(application);
        mIncomeRepo = new IncomeSummaryRepo(application);
        subscribe();
        //new GetAmountDataAsyncTask().execute();
    }

    public LiveData<List<IncomeData>> getList() {
        return mAmountData;
    }


    public void update() {
        // TODO implement
    }

    public LiveData<List<IncomeData>> get() {
        return mIncomeSources;
    }

    private void subscribe() {
        LiveData<List<IncomeSourceEntityBase>> incomeSourceEntities = mIncomeRepo.get();

        mIncomeSources =
                Transformations.switchMap(incomeSourceEntities,
                        new Function<List<IncomeSourceEntityBase>, LiveData<List<IncomeData>>>() {
                            @Override
                            public LiveData<List<IncomeData>> apply(List<IncomeSourceEntityBase> input) {
                                return getAllIncomeSources(input);
                            }
                        });
    }

    private LiveData<List<IncomeData>> getAllIncomeSources(List<IncomeSourceEntityBase> incomeSourceList) {
        RetirementOptionsEntity roe = mRetireRepo.get().getValue();
        return IncomeSummaryHelper.getIncomeSummary(incomeSourceList, roe);
    }

    /*
    private class GetAmountDataAsyncTask extends AsyncTask<Void, Void, List<IncomeData>> {

        @Override
        protected List<IncomeData> doInBackground(Void... voids) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeData> benefitData) {
            mAmountData.setValue(benefitData);
        }
    }

    private List<IncomeData> getAllIncomeSources() {
        RetirementOptionsEntity roe = mRetireRepo.get().getValue();
        return getIncomeSummary(roe);
    }
    */
}
