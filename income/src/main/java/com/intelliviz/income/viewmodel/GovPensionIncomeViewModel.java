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
import com.intelliviz.data.GovPensionEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.repo.GovEntityRepo;

import java.util.List;


/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeViewModel extends AndroidViewModel {
    private LiveData<GovPensionViewData> mViewData = new MutableLiveData<>();
    private GovEntityRepo mRepo;
    private LiveData<GovPensionEx> mSource;

    private static long mIncomeId;

    public GovPensionIncomeViewModel(Application application,
                                     GovEntityRepo govRepo,
                                     long incomeId,
                                     int owner) {
        super(application);
        mRepo = govRepo;
        mSource = mRepo.getGovPensionDataEx();
        subscribe(incomeId, owner);
        mIncomeId = incomeId;
        //mRepo.load();
    }

    private void subscribe(final long id, final int owner) {
        //LiveData<GovPensionEx> gpe = mRepo.getEx();
        mViewData = Transformations.switchMap(mSource,
                new Function<GovPensionEx, LiveData<GovPensionViewData>>() {
                    @Override
                    public LiveData<GovPensionViewData> apply(GovPensionEx input) {
                        List<GovPensionEntity> gpeList = input.getGpeList();
                        RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());
                        GovPensionHelper helper = new GovPensionHelper(getApplication(), gpeList, ro);
                        MutableLiveData<GovPensionViewData> ldata = new MutableLiveData();
                        ldata.setValue(helper.get(id, owner));
                        return ldata;
                    }
                });
    }

    public LiveData<GovPensionViewData> get() {
        return mViewData;
    }

    public void setData(GovPension gp) {
        mRepo.setData(GovPensionEntityMapper.map(gp));
    }

    public void update() {
        mRepo.load();
    }

    public void updateSpouseBirthdate(String birthdate) {
        mRepo.updateSpouseBirthdate(birthdate);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;
        private GovEntityRepo mRepo;
        private int mOwner;

        public Factory(@NonNull Application application, GovEntityRepo repo, long incomeId, int owner) {
            mApplication = application;
            mRepo = repo;
            mIncomeId = incomeId;
            mOwner = owner;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GovPensionIncomeViewModel(mApplication, mRepo, mIncomeId, mOwner);
        }
    }
}
