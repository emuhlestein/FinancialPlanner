package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * Created by edm on 10/7/2017.
 */

public class NavigationModelView extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<RetirementOptionsEntity> mROE = new MutableLiveData<>();

    public NavigationModelView(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetRetirementOptionsAsyncTask().execute();
    }

    public LiveData<RetirementOptionsEntity> getROE() {
        return mROE;
    }

    public void update(RetirementOptionsEntity roe) {
        new UpdateRetirementOptionsAsyncTask().execute(roe);
        mROE.setValue(roe);
    }

    public void updateBirthdate(String birthdate) {
        RetirementOptionsEntity rom = mROE.getValue();
        RetirementOptionsEntity newRom = new RetirementOptionsEntity(rom.getId(), rom.getEndAge(), rom.getCurrentOption(), birthdate);
        mROE.setValue(newRom);
        new UpdateRetirementOptionsAsyncTask().execute(newRom);
    }

    private class GetRetirementOptionsAsyncTask extends android.os.AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
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
}
