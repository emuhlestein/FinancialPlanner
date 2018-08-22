package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.GovPensionEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.List;

/**
 * Created by edm on 6/18/2018.
 */

public class GovEntityRepo {
    private AppDatabase mDB;
    private volatile static GovEntityRepo mINSTANCE;
    private MutableLiveData<GovPensionEntity> mGPE =
            new MutableLiveData<>();
    private MutableLiveData<List<GovPensionEntity>> mGpeList =
            new MutableLiveData<>();
    private MutableLiveData<RetirementOptionsEntity> mROE =
            new MutableLiveData<>();
    private MutableLiveData<GovPensionEx> mGpeEx = new MutableLiveData<>();

    public static GovEntityRepo getInstance(Application application) {
        if(mINSTANCE == null) {
            synchronized (GovEntityRepo.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = new GovEntityRepo(application);
                }
            }
        }
        return mINSTANCE;
    }

    GovEntityRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
    }

    public MutableLiveData<GovPensionEx> getGovPensionDataEx() {
        MutableLiveData<GovPensionEx> govPensionEx = new MutableLiveData<>();
        mGpeEx = govPensionEx;
        load();
        return govPensionEx;
    }

    public void load() {
        new GetExAsyncTask().execute();
    }

    public LiveData<List<GovPensionEntity>> get() {
        return mGpeList;
    }

    public LiveData<GovPensionEx> getEx() {
        return mGpeEx;
    }

    public LiveData<List<GovPensionEntity>> getList() {
        return mGpeList;
    }

    public List<GovPensionEntity> getImmediate() {
        return mDB.govPensionDao().get();
    }

    public void updateSpouseBirthdate(String birthdate) {
        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
    }

    public void setData(GovPensionEntity gpe) {
        if(gpe.getId() == 0) {
            new InsertAsyncTask().execute(gpe);
        } else {
            new UpdateAsyncTask().execute(gpe);
        }
    }

    public void delete(GovPensionEntity gpid) {
        new DeleteAsyncTask().execute(gpid);
    }

    public void update(GovPensionEntity gpid) {
        new UpdateAsyncTask().execute(gpid);
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, GovPensionEntity> {

        @Override
        protected GovPensionEntity doInBackground(Long... params) {
            return mDB.govPensionDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(GovPensionEntity gpe) {
            mGPE.setValue(gpe);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().update(params[0]);
        }
    }

    private class InsertAsyncTask extends AsyncTask<GovPensionEntity, Void, Long> {

        @Override
        protected Long doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().insert(params[0]);
        }
    }

    private class GetListAsyncTask extends AsyncTask<Long, Void, List<GovPensionEntity>> {

        @Override
        protected List<GovPensionEntity> doInBackground(Long... params) {
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            return gpeList;
        }

        @Override
        protected void onPostExecute(List<GovPensionEntity> gpeList) {
            mGpeList.setValue(gpeList);
        }
    }

    private class GetExAsyncTask extends AsyncTask<Void, Void, GovPensionEx> {

        @Override
        protected GovPensionEx doInBackground(Void... params) {
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new GovPensionEx(gpeList, roe);
        }

        @Override
        protected void onPostExecute(GovPensionEx gpeEx) {
            mGpeEx.setValue(gpeEx);
        }
    }

    private class DeleteAsyncTask extends android.os.AsyncTask<GovPensionEntity, Void, Void> {

        @Override
        protected Void doInBackground(GovPensionEntity... params) {
            mDB.govPensionDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    private class UpdateSpouseBirthdateAsyncTask extends AsyncTask<String, Void, GovPensionEx> {

        @Override
        protected GovPensionEx doInBackground(String... params) {
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return new GovPensionEx(gpeList, roe);
        }

        @Override
        protected void onPostExecute(GovPensionEx gpeEx) {
            mGpeEx.setValue(gpeEx);
        }
    }
}
