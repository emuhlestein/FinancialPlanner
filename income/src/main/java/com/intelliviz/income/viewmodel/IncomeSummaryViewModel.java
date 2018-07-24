package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeSummaryEx;
import com.intelliviz.data.IncomeSummaryHelper;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.IncomeSummaryRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private IncomeSummaryRepo mIncomeRepo;
    private LiveData<List<IncomeDetails>> mIncomeDetails = new MutableLiveData<>();

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mIncomeRepo = new IncomeSummaryRepo(application);
        subscribe();
        mIncomeRepo.update();
    }

    public void update() {
        mIncomeRepo.update();
        //new UpdateIncomeSummaryAsyncTask().execute();
    }

    public LiveData<List<IncomeDetails>> get() {
        return mIncomeDetails;
    }

    private void subscribe() {
        LiveData<IncomeSummaryEx> incomeSummaryEx = mIncomeRepo.get();

        mIncomeDetails =
                Transformations.switchMap(incomeSummaryEx,
                        new Function<IncomeSummaryEx, LiveData<List<IncomeDetails>>>() {
                            @Override
                            public LiveData<List<IncomeDetails>> apply(IncomeSummaryEx input) {
                                MutableLiveData<List<IncomeDetails>> ldata = new MutableLiveData<>();
                                RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());
                                ldata.setValue(getIncomeDetailsList(input.getIncomeSourceList(), ro));
                                return ldata;
                            }
                        });
    }

    private List<IncomeDetails> getIncomeDetailsList(List<IncomeSourceEntityBase> incomeSourceList, RetirementOptions ro) {
        List<IncomeData> incomeDataList = IncomeSummaryHelper.getIncomeSummary(incomeSourceList, ro);
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

   /* private class UpdateIncomeSummaryAsyncTask extends AsyncTask<Void, Void, List<IncomeDetails>> {

        @Override
        protected List<IncomeDetails> doInBackground(Void... voids) {
            RetirementOptionsEntity roe = mRetireRepo.getImmediate();
            List<IncomeSourceEntityBase> incomeSourceList = mIncomeRepo.getImmediate();
            List<IncomeDetails> detailsList = getIncomeDetailsList(incomeSourceList, RetirementOptionsMapper.map(roe));
            return detailsList;
        }

        @Override
        protected void onPostExecute(List<IncomeDetails> incomeDetails) {
            mIncomeDetails.setValue(incomeDetails);
        }
    }*/
}
