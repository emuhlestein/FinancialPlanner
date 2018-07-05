package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeData;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.GovEntityRepo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeDetailsViewModel extends AndroidViewModel {
    private LiveData<GovPension> mGP;
    private long mIncomeId;
    private LiveData<List<IncomeDetails>> mIncomeDetailsList = new MutableLiveData<>();
    private GovEntityRepo mRepo;

    public GovPensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mRepo = new GovEntityRepo(application, incomeId);
        mIncomeId = incomeId;
        subscribeToGovPensionEntityListChanges();
        subscribeToGovPensionEntityChanges();
    }

    public LiveData<List<IncomeDetails>> getList() {
        return mIncomeDetailsList;
    }

    private void subscribeToGovPensionEntityListChanges() {
        /*
        MutableLiveData<List<GovPensionEntity>> gpeList = mRepo.getList();
        mIncomeDetailsList = Transformations.map(gpeList,
                new Function<List<GovPensionEntity>, List<IncomeDetails>>() {
                    @Override
                    public List<IncomeDetails> apply(List<GovPensionEntity> govPensionEntities) {
                        return getIncomeDetails(govPensionEntities);
                    }
                });
                */
    }

    private void subscribeToGovPensionEntityChanges() {
        MutableLiveData<GovPensionEntity> gpe = mRepo.get();
        mGP = Transformations.map(gpe,
                new Function<GovPensionEntity, GovPension>() {
                    @Override
                    public GovPension apply(GovPensionEntity gpe) {
                        return GovPensionEntityMapper.map(gpe);
                    }
                });
    }

    public LiveData<GovPension> get() {
        return mGP;
    }

    public void setData(GovPension gp) {
        mRepo.setData(GovPensionEntityMapper.map(gp));
    }

    public void update() {
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;

        public Factory(@NonNull Application application, long incomeId) {
            mApplication = application;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GovPensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private List<IncomeDetails> getIncomeDetails(GovPension gp) {

        double monthlyBenefit = gp.getMonthlyBenefit();
        double fullMonthlyBenefit = Double.parseDouble(gp.getFullMonthlyBenefit());

        String message = "";
        boolean addMessage = false;
        if(monthlyBenefit > fullMonthlyBenefit) {
            addMessage = true;
            message = "Spousal benefits apply. Spouse cannot take benefits before principle spouse.";
        }

        List<IncomeData> listIncomeData = gp.getIncomeData();
        List<IncomeDetails> incomeDetails = new ArrayList<>();
        for(IncomeData incomeData : listIncomeData) {
            AgeData age = incomeData.getAge();
            String amount = SystemUtils.getFormattedCurrency(incomeData.getMonthlyAmount());
            String line1 = age.toString() + "   " + amount;
            IncomeDetails incomeDetail;

            if(addMessage) {
                incomeDetail = new IncomeDetails(line1, incomeData.getBalanceState(), message);
                incomeDetail.setAcceptClick(true);
                addMessage = false;
            } else {
                incomeDetail = new IncomeDetails(line1, incomeData.getBalanceState(), "");
            }

            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
}
