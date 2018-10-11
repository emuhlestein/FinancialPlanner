package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.repo.GovEntityRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;

import java.util.List;

/**
 * Created by edm on 2/12/2018.
 */

public class PersonalInfoViewModel extends AndroidViewModel {
    private LiveData<RetirementOptions> mRO = new MutableLiveData<>();
    private RetirementOptionsEntityRepo mRetireRepo;
    private GovEntityRepo mGovPensionRepo;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        mGovPensionRepo = GovEntityRepo.getInstance(application);
        loadData();
        subscribe();
    }

    public LiveData<RetirementOptions> get() {
        return mRO;
    }

    private void subscribe() {
        //mRO = mRetireRepo.get();
        /*

        mRO = Transformations.switchMap(roe,
                        new Function<RetirementOptions, LiveData<RetirementOptions>>() {
                            @Override
                            public LiveData<RetirementOptions> apply(RetirementOptionsEntity input) {
                                RetirementOptions ro = RetirementOptionsMapper.map(input);
                                MutableLiveData<RetirementOptions> ldata = new MutableLiveData<>();
                                ldata.setValue(ro);
                                return ldata;
                            }
                        });
                        */
    }

    public void update(RetirementOptions roe) {
        mRetireRepo.update(RetirementOptionsMapper.map(roe));
    }

    private class GetAsyncTask extends AsyncTask<Void, Void, RetirementOptions> {

        @Override
        protected RetirementOptions doInBackground(Void... params) {
            RetirementOptionsEntity roe = mRetireRepo.getImmediate();
            RetirementOptions ro = new RetirementOptions(roe.getId(), roe.getEndAge(), roe.getSpouseEndAge(),
                    roe.getBirthdate(), roe.getSpouseBirthdate(), roe.getIncludeSpouse(), roe.getCountryCode());
            List<GovPensionEntity> gpeList = mGovPensionRepo.getImmediate();
            if(!gpeList.isEmpty()) {
                ro.setCountryAvailable(false);
            }
            return ro;
        }

        @Override
        protected void onPostExecute(RetirementOptions roe) {
            ((MutableLiveData)mRO).setValue(roe);
        }
    }

    private void loadData() {
        new GetAsyncTask().execute();
    }
}
