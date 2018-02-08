package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * Created by edm on 10/7/2017.
 */

public class StartUpViewModel extends AndroidViewModel {
    public static final int BIRTHDATE_NOTSET  = 0;
    public static final int BIRTHDATE_INVALID = 1;
    public static final int BIRTHDATE_VALID   = 2;
    private MutableLiveData<AgeData> mCurrentAge = new MutableLiveData<>();
    private MutableLiveData<Integer> mValidBirthdate = new MutableLiveData<>();
    private MutableLiveData<RetirementOptionsEntity> mROE = new MutableLiveData<>();
    private AppDatabase mDB;

    public StartUpViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetRetirementOptionsAsyncTask().execute();
    }

    public MutableLiveData<RetirementOptionsEntity> get() {
        return mROE;
    }

    public void updateBirthdate(String birthdate, int includeSpouse, String spouseBirthdate) {
        RetirementOptionsEntity rom = mROE.getValue();
        RetirementOptionsEntity newRom = new RetirementOptionsEntity(rom.getId(), rom.getEndAge(), birthdate, includeSpouse, spouseBirthdate);
        mROE.setValue(newRom);
        new UpdateRetirementOptionsAsyncTask().execute(newRom);
    }

    private class GetRetirementOptionsAsyncTask extends AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            // TODO if rom is null, one needs to be added
            mROE.setValue(rom);
        }
    }

    private class UpdateRetirementOptionsAsyncTask extends android.os.AsyncTask<RetirementOptionsEntity, Void, Void> {

        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            RetirementOptionsEntity roe = params[0];
            mDB.retirementOptionsDao().update(roe);
            // TODO when ROM is updated, everything should be updated.
            // SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }

    public static class BirthdateInfo {
        private String mBirthdate;
        private int mStatus;

        public BirthdateInfo(String birthdate, int status) {
            mBirthdate = birthdate;
            mStatus = status;
        }

        public String getBirthdate() {
            return mBirthdate;
        }

        public int getStatus() {
            return mStatus;
        }
    }
}
