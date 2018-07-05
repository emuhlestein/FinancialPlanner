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
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.List;


/**
 * Created by edm on 9/30/2017.
 */

public class PensionIncomeEditViewModel extends AndroidViewModel {
    private LiveData<PensionData> mPD =
            new MutableLiveData<>();
    private PensionIncomeEntityRepo mRepo;
    private RetirementOptionsEntityRepo mROERepo;
    private long mIncomeId;

    public PensionIncomeEditViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mROERepo = new RetirementOptionsEntityRepo(application);
        mRepo = new PensionIncomeEntityRepo(application, incomeId);
        subscribeToPensionEntityChanges();
    }

    public LiveData<PensionData> getData() {
        return mPD;
    }

    public LiveData<List<PensionIncomeEntity>> getList() {
        return null;
    }

    public void setData(PensionData pd) {
        mRepo.setData(PensionDataEntityMapper.map(pd));
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

    public void delete(PensionData pd) {
        mRepo.delete(PensionDataEntityMapper.map(pd));
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
            return (T) new PensionIncomeEditViewModel(mApplication, mIncomeId);
        }
    }
}
