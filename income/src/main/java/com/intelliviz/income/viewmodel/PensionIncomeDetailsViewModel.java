package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.repo.PensionIncomeEntityRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.List;


/**
 * Created by edm on 11/21/2017.
 */

public class PensionIncomeDetailsViewModel extends AndroidViewModel {
    private LiveData<PensionData> mPIE =
            new MutableLiveData<>();
    private long mIncomeId;
    private MutableLiveData<List<IncomeData>> mBenefitDataList = new MutableLiveData<List<IncomeData>>();
    private PensionIncomeEntityRepo mRepo;
    private RetirementOptionsEntityRepo mROERepo;

    public PensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mROERepo = new RetirementOptionsEntityRepo(application);
        mRepo = new PensionIncomeEntityRepo(application);
        subscribeToPensionEntityChanges();
    }

    public MutableLiveData<List<IncomeData>> getList() {
        return mBenefitDataList;
    }

    public LiveData<PensionData> get() {
        return mPIE;
    }

    public void setData(PensionData pd) {
       mRepo.setData(PensionDataEntityMapper.map(pd));
    }

    public void update(PensionData pd) {
        mRepo.update(pd);
    }

    public void delete(PensionData gp) {
        mRepo.delete(PensionDataEntityMapper.map(gp));
    }

    private void subscribeToPensionEntityChanges() {
        MutableLiveData<PensionIncomeEntity> gpe = mRepo.get();
        mPIE = Transformations.map(gpe,
                new Function<PensionIncomeEntity, PensionData>() {
                    @Override
                    public PensionData apply(PensionIncomeEntity pie) {
                        return PensionDataEntityMapper.map(pie);
                    }
                });
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
            return (T) new PensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<IncomeData>> {

        @Override
        protected List<IncomeData> doInBackground(Long... params) {
            long id = params[0];
            return getBenefitData(id);
        }

        @Override
        protected void onPostExecute(List<IncomeData> benefitDataList) {
            mBenefitDataList.setValue(benefitDataList);
        }
    }

    private List<IncomeData> getBenefitData(long id) {
        PensionIncomeEntity entity = mRepo.get().getValue();
        RetirementOptionsEntity rod = mROERepo.get().getValue();
        String birthdate = rod.getBirthdate();
        AgeData endAge = rod.getEndAge();
        AgeData minAge = entity.getMinAge();
        String monthlyBenefit = entity.getMonthlyBenefit();

        PensionRules rules = new PensionRules(birthdate, minAge, endAge, monthlyBenefit);
        entity.setRules(rules);

        return entity.getIncomeData();
    }
}
