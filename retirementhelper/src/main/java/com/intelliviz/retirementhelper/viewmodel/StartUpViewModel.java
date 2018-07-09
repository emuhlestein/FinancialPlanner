package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

/**
 * Created by edm on 10/7/2017.
 */

public class StartUpViewModel extends AndroidViewModel {
    public static final int BIRTHDATE_NOTSET = 0;
    public static final int BIRTHDATE_INVALID = 1;
    public static final int BIRTHDATE_VALID = 2;
    private MutableLiveData<AgeData> mCurrentAge = new MutableLiveData<>();
    private MutableLiveData<Integer> mValidBirthdate = new MutableLiveData<>();
    private LiveData<RetirementOptions> mROE = new MutableLiveData<>();
    private RetirementOptionsEntityRepo mRetireRepo;

    public StartUpViewModel(Application application) {
        super(application);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        subscribeToRetireOptionsEntityChanges();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new StartUpViewModel(mApplication);
        }
    }

    public LiveData<RetirementOptions> get() {
        return mROE;
    }

    public void updateBirthdate(String birthdate) {
        mRetireRepo.updateBirthdate(birthdate);
    }

    public void updateSpouseBirthdate(String birthdate) {
        mRetireRepo.updateSpouseBirthdate(birthdate);
    }

    public void update(RetirementOptions ro) {
        RetirementOptionsEntity roe = new RetirementOptionsEntity(ro.getId(), ro.getEndAge(), ro.getBirthdate(), ro.getIncludeSpouse(), ro.getSpouseBirthdate(), ro.getCountryCode());
        mRetireRepo.update(roe);
    }

    private void subscribeToRetireOptionsEntityChanges() {
        LiveData<RetirementOptionsEntity> gpe = mRetireRepo.get();
        mROE = Transformations.map(gpe,
                new Function<RetirementOptionsEntity, RetirementOptions>() {
                    @Override
                    public RetirementOptions apply(RetirementOptionsEntity roe) {
                        return new RetirementOptions(roe.getId(), roe.getEndAge(), roe.getBirthdate(), roe.getSpouseBirthdate(), roe.getIncludeSpouse(), roe.getCountryCode());
                    }
                });
    }
}
