package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.PensionDataEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PensionIncomeEntityRepo {
    private volatile static PensionIncomeEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private MutableLiveData<PensionIncomeEntity> mPIE =
            new MutableLiveData<>();
    private MutableLiveData<List<PensionIncomeEntity>> mPensionList =
            new MutableLiveData<>();
    private MutableLiveData<PensionDataEx> mPdEx;
    private Map<String, OnDataChangedListener> mListeners = new HashMap<>();

    public interface OnDataChangedListener {
        void onDataChanged(PensionDataEx pensionDataEx);
    }

    public static PensionIncomeEntityRepo getInstance(Application application) {
        if(mINSTANCE == null) {
            synchronized (PensionIncomeEntityRepo.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = new PensionIncomeEntityRepo(application);
                }
            }
        }
        return mINSTANCE;
    }

    private PensionIncomeEntityRepo(Application application) {
        mDB = AppDatabase.getInstance(application);
        new GetListAsyncTask().execute();
        mPdEx = new MutableLiveData<>();
    }

    public void addListener(String key, OnDataChangedListener listener) {
        if(mListeners.containsKey(key)) {
            mListeners.remove(key);
        }
        mListeners.put(key, listener);
    }


    public MutableLiveData<PensionDataEx> getPensionDataEx(long id) {
        //load(id);
        return mPdEx;
    }

    public void load(long id) {
        new GetExAsyncTask().execute(id);
    }

    public MutableLiveData<PensionIncomeEntity> get() {
        return mPIE;
    }

    public LiveData<PensionDataEx> getEx() {
        return mPdEx;
    }

    public MutableLiveData<List<PensionIncomeEntity>> getList() {
        return mPensionList;
    }

    public void setData(PensionIncomeEntity pie) {
        if(pie.getId() == 0) {
            new InsertAsyncTask().execute(pie);
        } else {
            new UpdateAsyncTask().execute(pie);
        }
    }

    public void delete(PensionIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
    }

    public void update(PensionIncomeEntity pie) {
        new UpdateAsyncTask().execute(pie);
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, PensionIncomeEntity> {

        @Override
        protected PensionIncomeEntity doInBackground(Long... params) {
            PensionIncomeEntity pie = mDB.pensionIncomeDao().get(params[0]);
            return pie;
        }

        @Override
        protected void onPostExecute(PensionIncomeEntity pid) {
            mPIE.setValue(pid);
        }
    }

    private class GetExAsyncTask extends AsyncTask<Long, Void, PensionDataEx> {

        @Override
        protected PensionDataEx doInBackground(Long... params) {
            PensionIncomeEntity pie = mDB.pensionIncomeDao().get(params[0]);
            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new PensionDataEx(pie, pieList.size(), roe);
        }

        @Override
        protected void onPostExecute(PensionDataEx pdEx) {
            //mPdEx.setValue(pdEx);
            for(OnDataChangedListener listener : mListeners.values()) {
                listener.onDataChanged(pdEx);
            }
        }
    }

    private class GetListAsyncTask extends AsyncTask<Long, Void, List<PensionIncomeEntity>> {

        @Override
        protected List<PensionIncomeEntity> doInBackground(Long... params) {
            List<PensionIncomeEntity> gpeList = mDB.pensionIncomeDao().get();
            return gpeList;
        }

        @Override
        protected void onPostExecute(List<PensionIncomeEntity> gpeList) {
            mPensionList.setValue(gpeList);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<PensionIncomeEntity, Void, PensionDataEx> {

        @Override
        protected PensionDataEx doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().update(params[0]);
            PensionIncomeEntity pie = mDB.pensionIncomeDao().get(params[0].getId());
            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new PensionDataEx(pie, pieList.size(), roe);
        }

        @Override
        protected void onPostExecute(PensionDataEx pdEx) {
            for(OnDataChangedListener listener : mListeners.values()) {
                listener.onDataChanged(pdEx);
            }
        }
    }

    private class InsertAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(PensionIncomeEntity... params) {
            return mDB.pensionIncomeDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().delete(params[0]);
            return null;
        }
    }
}
