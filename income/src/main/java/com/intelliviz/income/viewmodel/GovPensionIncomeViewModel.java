package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.GovPensionEx;
import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.GovEntityRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeViewModel extends AndroidViewModel {
    private LiveData<GovPension> mGP;
    private LiveData<List<IncomeDetails>> mIncomeDetailsList = new MutableLiveData<>();
    private GovEntityRepo mRepo;
    private RetirementOptionsEntityRepo mRetireOptionsRepo;
    private LiveData<RetirementOptions> mRO;
    private LiveData<List<GovPension>> mGpeList;
    private MediatorLiveData mLiveDataMerger = new MediatorLiveData<>();

    public GovPensionIncomeViewModel(Application application, long incomeId) {
        super(application);
        mRetireOptionsRepo = RetirementOptionsEntityRepo.getInstance(application);
        //subscribeRetirementOptions();
        //subscribeToGovPensionEntityListChanges();
        mRepo = GovEntityRepo.getInstance(application);
        subscribe(incomeId);
    }

//    public LiveData<List<IncomeDetails>> getList() {
//        return mIncomeDetailsList;
//    }

    private void subscribeToGovPensionEntityListChanges() {
        LiveData<List<GovPensionEntity>> gpeList = mRepo.getList();
        mGpeList = Transformations.switchMap(gpeList,
                new Function<List<GovPensionEntity>, LiveData<List<GovPension>>>() {
                    @Override
                    public LiveData<List<GovPension>> apply(List<GovPensionEntity> govPensionEntities) {
                        List<GovPension> gpList = new ArrayList<>();
                        for(GovPensionEntity gpe : govPensionEntities) {
                            GovPension gp = GovPensionEntityMapper.map(gpe);
                            gpList.add(gp);
                        }
                        MutableLiveData<List<GovPension>> ldata = new MutableLiveData();
                        ldata.setValue(gpList);
                        return ldata;
                    }
                });
    }

    private void subscribeRetirementOptions() {
        LiveData<RetirementOptionsEntity> roe = mRetireOptionsRepo.get();
        mRO = Transformations.switchMap(roe,
                new Function<RetirementOptionsEntity, LiveData<RetirementOptions>>() {
                    @Override
                    public LiveData<RetirementOptions> apply(RetirementOptionsEntity input) {
                        MutableLiveData<RetirementOptions> ldata = new MutableLiveData<>();
                        ldata.setValue(RetirementOptionsMapper.map(input));
                        return ldata;
                    }
                });
    }

    private void subscribe(final long id) {
        LiveData<GovPensionEx> gpe = mRepo.getEx();
        mGP = Transformations.switchMap(gpe,
                new Function<GovPensionEx, LiveData<GovPension>>() {
                    @Override
                    public LiveData<GovPension> apply(GovPensionEx input) {
                        List<GovPensionEntity> gpeList = input.getGpeList();
                        RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());

                        List<GovPension> gpList = new ArrayList<>();
                        for(GovPensionEntity gpe : gpeList) {
                            GovPension gp = GovPensionEntityMapper.map(gpe);
                            gpList.add(gp);
                        }

                        if(id == 0 && gpList.size() < 2) {
                            GovPension gp = new GovPension(0, RetirementConstants.INCOME_TYPE_GOV_PENSION, "",
                                    "0", new AgeData(65, 0), false);
                            gpList.add(gp);
                        }
                        SocialSecurityRules.setRulesOnGovPensionEntities(gpList, ro);
                        GovPension gp = getGovPension(gpList, id);
                        MutableLiveData<GovPension> ldata = new MutableLiveData();
                        ldata.setValue(gp);
                        return ldata;
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
            return (T) new GovPensionIncomeViewModel(mApplication, mIncomeId);
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

    private GovPension getGovPension(List<GovPension> gpList, long id) {
        if(gpList == null || gpList.isEmpty()) {
            return null;
        }

        if(gpList.size() == 1) {
            return gpList.get(0);
        } else {
            if(gpList.get(0).getId() == id) {
                return gpList.get(0);
            } else {
                return gpList.get(1);
            }
        }
    }
}
