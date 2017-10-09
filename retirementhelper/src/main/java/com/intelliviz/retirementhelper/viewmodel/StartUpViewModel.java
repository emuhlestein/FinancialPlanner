package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 10/7/2017.
 */

public class StartUpViewModel extends AndroidViewModel {
    public static final int BIRTHDATE_NOTSET  = 0;
    public static final int BIRTHDATE_INVALID = 1;
    public static final int BIRTHDATE_VALID   = 2;
    private MutableLiveData<BirthdateInfo> mBirthdateInfo = new MutableLiveData<>();
    private MutableLiveData<AgeData> mCurrentAge = new MutableLiveData<>();
    private MutableLiveData<Integer> mValidBirthdate = new MutableLiveData<>();
    private RetirementOptionsEntity mROM;
    private AppDatabase mDB;

    public StartUpViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetRetirementOptionsAsyncTask().execute();
    }

    public LiveData<BirthdateInfo> getBirthdate() {
        return mBirthdateInfo;
    }

    public void updateBirthdate(String birthdate) {
        new UpdateBirthdateAsyncTask().execute(birthdate);
    }

    private class GetRetirementOptionsAsyncTask extends AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            // TODO if rom is null, one needs to be added
            mROM = rom;
            mCurrentAge.setValue(SystemUtils.getAge(mROM.getBirthdate()));
            if(SystemUtils.validateBirthday(mROM.getBirthdate())) {
                mBirthdateInfo.setValue(new BirthdateInfo(mROM.getBirthdate(), BIRTHDATE_VALID));
            } else {
                mBirthdateInfo.setValue(new BirthdateInfo(mROM.getBirthdate(), BIRTHDATE_INVALID));
            }
        }
    }

    private class UpdateBirthdateAsyncTask extends AsyncTask<String, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(String... params) {
            RetirementOptionsEntity rom = mDB.retirementOptionsDao().get();
            rom.setBirthdate(params[0]);
            mDB.retirementOptionsDao().update(rom);
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            mROM = rom;
            mCurrentAge.setValue(SystemUtils.getAge(mROM.getBirthdate()));
            if(SystemUtils.validateBirthday(mROM.getBirthdate())) {
                mBirthdateInfo.setValue(new BirthdateInfo(mROM.getBirthdate(), BIRTHDATE_VALID));
            } else {
                mBirthdateInfo.setValue(new BirthdateInfo(mROM.getBirthdate(), BIRTHDATE_INVALID));
            }
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
