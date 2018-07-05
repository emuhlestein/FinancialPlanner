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

import com.intelliviz.data.SavingsData;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.repo.RetirementOptionsEntityRepo;
import com.intelliviz.repo.SavingsIncomeEntityRepo;


/**
 * Created by edm on 9/30/2017.
 */

public class SavingsIncomeEditViewModel extends AndroidViewModel {
    private LiveData<SavingsData> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<RetirementOptionsEntity> mROE =
            new MutableLiveData<>();
    private long mId;
    private SavingsIncomeEntityRepo mRepo;
    private RetirementOptionsEntityRepo mRetireRepo;

    public SavingsIncomeEditViewModel(Application application, long incomeId) {
        super(application);
        mRepo = new SavingsIncomeEntityRepo(application, incomeId);
        mRetireRepo = new RetirementOptionsEntityRepo(application);
        subscribeSavingsIncomeEntityChanges();
    }

    public LiveData<SavingsData> getData() {
        return mSIE;
    }

    public void setData(SavingsData sie) {
        mRepo.setData(SavingsDataEntityMapper.map(sie));
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
            return (T) new SavingsIncomeEditViewModel(mApplication, mIncomeId);
        }
    }

    private void subscribeSavingsIncomeEntityChanges() {
        MutableLiveData<SavingsIncomeEntity> sie = mRepo.get();
        mSIE = Transformations.map(sie,
                new Function<SavingsIncomeEntity, SavingsData>() {
                    @Override
                    public SavingsData apply(SavingsIncomeEntity sie) {
                        return SavingsDataEntityMapper.map(sie);
                    }
                });
    }
}
