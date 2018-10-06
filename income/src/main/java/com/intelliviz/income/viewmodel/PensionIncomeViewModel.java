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

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionDataEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.repo.PensionIncomeEntityRepo;


/**
 * Created by edm on 9/30/2017.
 */

public class PensionIncomeViewModel extends AndroidViewModel {
    private LiveData<PensionViewData> mViewData;
    private PensionIncomeEntityRepo mRepo;
    private LiveData<PensionDataEx> mSource;
    private long mId;
    private int mStatus = -1;

    public PensionIncomeViewModel(Application application, long incomeId) {
        super(application);
        mRepo = PensionIncomeEntityRepo.getInstance(application);
        mSource = mRepo.getPensionDataEx(incomeId);
        subscribe(incomeId);
        mRepo.load(incomeId);
        mId = incomeId;
    }

    public LiveData<PensionViewData> get() {
        return mViewData;
    }

    private void subscribe(final long id) {
        mViewData = Transformations.switchMap(mSource,
                new Function<PensionDataEx, LiveData<PensionViewData>>() {
                    @Override
                    public LiveData<PensionViewData> apply(PensionDataEx input) {
                        RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());

                        PensionData pd = null;
                        if(input.getPie() != null) {
                            pd = PensionDataEntityMapper.map(input.getPie());
                        }
                        PensionIncomeHelper helper = new PensionIncomeHelper(getApplication(), pd, ro, input.getNumRecords());
                        MutableLiveData<PensionViewData> ldata = new MutableLiveData<>();
                        PensionViewData pensionViewData = helper.get(id);
                        int status = pensionViewData.getStatus();
                        if(status == mStatus) {
                            mStatus = -1;
                        } else {
                            mStatus = status;
                        }
                        ldata.setValue(helper.get(id));
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

    public void setData(PensionData pd) {
        mRepo.setData(PensionDataEntityMapper.map(pd));
    }

    public void update() {
        mRepo.load(mId);
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
            return (T) new PensionIncomeViewModel(mApplication, mIncomeId);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
