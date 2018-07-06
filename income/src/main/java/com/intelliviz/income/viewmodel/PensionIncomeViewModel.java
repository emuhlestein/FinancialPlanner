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
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.repo.PensionIncomeEntityRepo;


/**
 * Created by edm on 9/30/2017.
 */

public class PensionIncomeViewModel extends AndroidViewModel {
    private LiveData<PensionData> mPD = new MutableLiveData<>();
    private PensionIncomeEntityRepo mRepo;

    public PensionIncomeViewModel(Application application, long incomeId) {
        super(application);
        mRepo = PensionIncomeEntityRepo.getInstance(application);
        //subscribeToPensionEntityChanges();
        subscribe(incomeId);
    }

    public LiveData<PensionData> get() {
        return mPD;
    }

    private void subscribe(long id) {
        MutableLiveData<PensionIncomeEntity> entity = mRepo.get(id);
        mPD = Transformations.switchMap(entity,
                new Function<PensionIncomeEntity, LiveData<PensionData>>() {

                    @Override
                    public LiveData<PensionData> apply(PensionIncomeEntity input) {
                        MutableLiveData<PensionData> ldata = new MutableLiveData<>();
                        ldata.setValue(PensionDataEntityMapper.map(input));
                        return ldata;
                    }
                });
    }

    public void setData(PensionData pd) {
        mRepo.setData(PensionDataEntityMapper.map(pd));
    }

    public void update() {
//        PensionData pd = mPD.getValue();
//        if(pd != null) {
//            mRepo.setData(PensionDataEntityMapper.map(pd));
//        }
    }

    private void subscribeToPensionEntityChanges() {
        MutableLiveData<PensionIncomeEntity> pie = mRepo.get();
        mPD = Transformations.map(pie,
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
            return (T) new PensionIncomeViewModel(mApplication, mIncomeId);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
