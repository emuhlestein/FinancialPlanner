package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * Created by edm on 2/12/2018.
 */

public class PersonalInfoViewModel extends AndroidViewModel {
    private MutableLiveData<RetirementOptionsEntity> mROE =
            new MutableLiveData<>();
    private AppDatabase mDB;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
    }

    public MutableLiveData<RetirementOptionsEntity> get() {
        return mROE;
    }

    public void update(RetirementOptionsEntity roe) {

    }

    private class GetAsyncTask extends android.os.AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity roe) {
            mROE.setValue(roe);
        }
    }

    private class UpdateAsyncTask extends android.os.AsyncTask<RetirementOptionsEntity, Void, Void> {

        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            mDB.retirementOptionsDao().update(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }
}
