package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;

/**
 * Created by Ed Muhlestein on 6/21/2018.
 *
 */

public class RetirementOptionsEntityRepo {
    private volatile static RetirementOptionsEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private MutableLiveData<RetirementOptions> mROE =
            new MutableLiveData<>();
    private MutableLiveData<RetirementOptions> mRO =
            new MutableLiveData<>();

    public static RetirementOptionsEntityRepo getInstance(Application application) {
        if(mINSTANCE == null) {
            synchronized (RetirementOptionsEntityRepo.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = new RetirementOptionsEntityRepo(application);
                }
            }
        }
        return mINSTANCE;
    }

    private RetirementOptionsEntityRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute();
    }

    public LiveData<RetirementOptions> get() {
        //new GetAsyncTask().execute();
        return mRO;
    }

    /**
     * Return the retirement options immediately. This must not be called in the main thread.
     * @return the retirement options object.
     */
    public RetirementOptionsEntity getImmediate() {
        return mDB.retirementOptionsDao().get();
    }

    public void update(RetirementOptions ro) {
        RetirementOptionsEntity roe = RetirementOptionsMapper.map(ro);
        mROE.setValue(ro);
        new UpdateAsyncTask().execute(roe);
    }

    public void updateSpouseBirthdate(String birthdate) {
        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
    }

    public void updateBirthdate(String birthdate) {
        new UpdateBirthdateAsyncTask().execute(birthdate);
    }

    private class GetAsyncTask extends AsyncTask<Void, Void, RetirementOptions> {

        @Override
        protected RetirementOptions doInBackground(Void... params) {
            return RetirementOptionsMapper.map(mDB.retirementOptionsDao().get());
        }

        @Override
        protected void onPostExecute(RetirementOptions roe) {
            mRO.setValue(roe);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<RetirementOptionsEntity, Void, Void> {

        @Override
        protected Void doInBackground(RetirementOptionsEntity... params) {
            mDB.retirementOptionsDao().update(params[0]);
            return null;
        }
    }

    private class UpdateSpouseBirthdateAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return null;
        }
    }

    private class UpdateBirthdateAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return null;
        }
    }
}
