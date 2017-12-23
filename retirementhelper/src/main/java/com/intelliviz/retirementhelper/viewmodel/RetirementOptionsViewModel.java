package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * Created by edm on 12/22/2017.
 */

public class RetirementOptionsViewModel extends AndroidViewModel {
    private MutableLiveData<RetirementOptionsEntity> mROE = new MutableLiveData<>();
    private AppDatabase mDB;

    public RetirementOptionsViewModel(@NonNull Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute();
    }

    public MutableLiveData<RetirementOptionsEntity> get() {
        return mROE;
    }

    public void put(RetirementOptionsEntity roe) {
        new PutAsyncTask().execute(roe);
    }

    private class GetAsyncTask extends AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected RetirementOptionsEntity doInBackground(Void... voids) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return roe;
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity roe) {
            mROE.setValue(roe);
        }
    }

    private class PutAsyncTask extends AsyncTask<RetirementOptionsEntity, Void, Void> {
        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            mDB.retirementOptionsDao().update(params[0]);
            return null;
        }
    }
}
