package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeSummaryHelper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.IncomeSummaryRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private RetirementOptionsEntityRepo mRetireRepo;
    private LiveData<List<IncomeData>> mIncomeSources = new MutableLiveData<>();
    private RetirementOptionsEntity mROE;
    private IncomeSummaryRepo mIncomeRepo;
    private MutableLiveData<List<IncomeDetails>> mIncomeDetails = new MutableLiveData<>();

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        mIncomeRepo = new IncomeSummaryRepo(application);
        subscribe();
        mIncomeRepo.update();
        new UpdateIncomeSummaryAsyncTask().execute();
    }

    public void update() {
        mIncomeRepo.update();
        new UpdateIncomeSummaryAsyncTask().execute();
    }

    public LiveData<List<IncomeData>> get() {
        return mIncomeSources;
    }

    public LiveData<List<IncomeDetails>> getIncomeSources() {
        return mIncomeDetails;
    }

    private void subscribe() {
        LiveData<List<IncomeSourceEntityBase>> incomeSourceEntities = mIncomeRepo.get();

        mIncomeSources =
                Transformations.switchMap(incomeSourceEntities,
                        new Function<List<IncomeSourceEntityBase>, LiveData<List<IncomeData>>>() {
                            @Override
                            public LiveData<List<IncomeData>> apply(List<IncomeSourceEntityBase> input) {
                                MutableLiveData<List<IncomeData>> ldata = new MutableLiveData<>();
                                ldata.setValue(getAllIncomeSources(input));
                                return ldata;
                            }
                        });
    }

    private List<IncomeData> getAllIncomeSources(List<IncomeSourceEntityBase> incomeSourceList) {
        RetirementOptionsEntity roe = mRetireRepo.get().getValue();
        return IncomeSummaryHelper.getIncomeSummary(incomeSourceList, roe);
    }

    private List<IncomeDetails> getIncomeDetailsList(List<IncomeSourceEntityBase> incomeSourceList, RetirementOptionsEntity roe) {
        List<IncomeData> incomeDataList = IncomeSummaryHelper.getIncomeSummary(incomeSourceList, roe);
        if(incomeDataList == null) {
            return Collections.emptyList();
        }

        List<IncomeDetails> incomeDetails = new ArrayList<>();

        for (IncomeData benefitData : incomeDataList) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
            String line1 = age.toString() + "   " + amount + "  " + balance;
            IncomeDetails incomeDetail = new IncomeDetails(line1, RetirementConstants.BALANCE_STATE_GOOD, "");
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }

    private class UpdateIncomeSummaryAsyncTask extends AsyncTask<Void, Void, List<IncomeDetails>> {

        @Override
        protected List<IncomeDetails> doInBackground(Void... voids) {
            RetirementOptionsEntity roe = mRetireRepo.getImmediate();
            List<IncomeSourceEntityBase> incomeSourceList = mIncomeRepo.getImmediate();
            List<IncomeDetails> detailsList = getIncomeDetailsList(incomeSourceList, roe);
            return detailsList;
        }

        @Override
        protected void onPostExecute(List<IncomeDetails> incomeDetails) {
            mIncomeDetails.setValue(incomeDetails);
        }
    }
}
