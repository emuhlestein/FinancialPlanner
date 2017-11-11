package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 10/7/2017.
 */

public class NavigationModelView extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<RetirementOptionsEntity> mROM = new MutableLiveData<>();

    public NavigationModelView(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetRetirementOptionsAsyncTask().execute();
    }

    public LiveData<RetirementOptionsEntity> getROM() {
        return mROM;
    }

    public void update(int id, RetirementOptionsData rod) {
        RetirementOptionsEntity rom = new RetirementOptionsEntity(id, rod.getEndAge(), rod.getWithdrawMode(), rod.getWithdrawAmount(), rod.getBirthdate(), rod.getPercentIncrease());
        new UpdateRetirementOptionsAsyncTask().execute(rom);
        RetirementOptionsEntity r =  mROM.getValue();
        Log.d("edm", r.getBirthdate());
        mROM.setValue(rom);
    }

    public void updateBirthdate(String birthdate) {
        RetirementOptionsEntity rom = mROM.getValue();
        RetirementOptionsEntity newRom = new RetirementOptionsEntity(rom.getId(), rom.getEndAge(), rom.getWithdrawMode(), rom.getWithdrawAmount(), birthdate, rom.getPercentIncrease());
        mROM.setValue(newRom);
        new UpdateRetirementOptionsAsyncTask().execute(newRom);
    }

    private class GetRetirementOptionsAsyncTask extends android.os.AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            mROM.setValue(rom);
        }
    }

    private class UpdateRetirementOptionsAsyncTask extends android.os.AsyncTask<RetirementOptionsEntity, Void, Void> {

        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            mDB.retirementOptionsDao().update(params[0]);
            // TODO when ROM is updated, everything should be updated.
            SystemUtils.updateAppWidget(getApplication());
            return null;
        }
    }
}
