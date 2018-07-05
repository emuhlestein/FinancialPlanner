package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.RetirementOptionsEntity;

/**
 * Created by Ed Muhlestein on 6/21/2018.
 *
 */

public class RetirementOptionsEntityRepo {
    private AppDatabase mDB;
    private MutableLiveData<RetirementOptionsEntity> mROE =
            new MutableLiveData<>();

    public RetirementOptionsEntityRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute();
    }

    public MutableLiveData<RetirementOptionsEntity> get() {
        return mROE;
    }

    public void update(RetirementOptionsEntity roe) {
        mROE.setValue(roe);
        new UpdateAsyncTask().execute(roe);
    }

    public void updateSpouseBirthdate(String birthdate) {
        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
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

    private class UpdateAsyncTask extends AsyncTask<RetirementOptionsEntity, Void, Void> {

        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            mDB.retirementOptionsDao().update(params[0]);
            return null;
        }
    }

    private class UpdateSpouseBirthdateAsyncTask extends android.os.AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return null;
        }
    }
}
