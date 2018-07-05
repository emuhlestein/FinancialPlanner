package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

/**
 * Created by edm on 2/12/2018.
 */

public class PersonalInfoViewModel extends AndroidViewModel {
    private LiveData<RetirementOptions> mROE = new MutableLiveData<>();
    private RetirementOptionsEntityRepo mRetireRepo;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        mRetireRepo = new RetirementOptionsEntityRepo(application);
    }

    public LiveData<RetirementOptions> get() {
        LiveData<RetirementOptionsEntity> roe = mRetireRepo.get();

        LiveData<RetirementOptions> retireOptions =
                Transformations.switchMap(roe,
                        new Function<RetirementOptionsEntity, LiveData<RetirementOptions>>() {
                            @Override
                            public LiveData<RetirementOptions> apply(RetirementOptionsEntity input) {
                                RetirementOptions ro = RetirementOptionsMapper.map(input);
                                MutableLiveData<RetirementOptions> ldata = new MutableLiveData<>();
                                ldata.setValue(ro);
                                return ldata;
                            }
                        });
        return retireOptions;
    }

    public void update(RetirementOptions roe) {
        mRetireRepo.update(RetirementOptionsMapper.map(roe));
    }
}
