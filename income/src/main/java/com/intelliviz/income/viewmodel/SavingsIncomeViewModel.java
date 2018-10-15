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
import android.util.Log;

import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsDataEx;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.income.data.SavingsViewData;
import com.intelliviz.repo.SavingsIncomeEntityRepo;

import java.util.List;


/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeViewModel extends AndroidViewModel {
    private LiveData<SavingsViewData> mViewData = new MutableLiveData<>();
    private LiveData<SavingsDataEx> mSource;
    private SavingsIncomeEntityRepo mRepo;
    private LiveData<List<IncomeDetails>> mIncomeDetailsList = new MutableLiveData<>();
    private long mId;
    private int mStatus = -1;

    public SavingsIncomeViewModel(Application application, long incomeId, int incomeType) {
        super(application);
        mRepo = SavingsIncomeEntityRepo.getInstance(application);
        mSource = mRepo.getSavingsDataEx(incomeId);
        subscribe(incomeId, incomeType);
        mId = incomeId;
    }

    private void subscribe(final long id, final int incomeType) {
        mViewData = Transformations.switchMap(mSource,
                new Function<SavingsDataEx, LiveData<SavingsViewData>>() {
                    @Override
                    public LiveData<SavingsViewData> apply(SavingsDataEx input) {
                        RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());
                        SavingsData sd = null;
                        if(input.getSie() != null) {
                            sd = SavingsDataEntityMapper.map(input.getSie());
                        }

                        if(sd == null) {
                            Log.d("SavingsIncomeViewModel", "HERE");
                        }
                        SavingsIncomeHelper helper = new SavingsIncomeHelper(sd, ro, input.getNumRecords());
                        SavingsViewData savingsViewData = helper.get(id, incomeType);
                        int status = savingsViewData.getStatus();
                        if(status == mStatus) {
                            mStatus = -1;
                        } else {
                            mStatus = status;
                        }

                        MutableLiveData<SavingsViewData> ldata = new MutableLiveData();
                        ldata.setValue(helper.get(id, incomeType));
                        return ldata;
                    }
                });
    }

    public int getStatus() {
        return mStatus;
    }

    public boolean isStatusValid() {
        return (mStatus != -1);
    }

    public void setHandled() {
        mStatus = -1;
    }

    public LiveData<SavingsViewData> get() {
        return mViewData;
    }

    public LiveData<List<IncomeDetails>> getList() {
        return mIncomeDetailsList;
    }

    public void update() {
      mRepo.load(mId);
    }

    public void setData(SavingsData sie) {
        mRepo.setData(SavingsDataEntityMapper.map(sie));
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;
        private int mIncomeType;

        public Factory(@NonNull Application application, long incomeId, int incomeType) {
            mApplication = application;
            mIncomeId = incomeId;
            mIncomeType = incomeType;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SavingsIncomeViewModel(mApplication, mIncomeId, mIncomeType);
        }
    }
}
