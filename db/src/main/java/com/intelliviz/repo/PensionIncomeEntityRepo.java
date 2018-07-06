package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.PensionRules;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.List;

public class PensionIncomeEntityRepo {
    private volatile static PensionIncomeEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private MutableLiveData<PensionIncomeEntity> mPIE =
            new MutableLiveData<>();
    private MutableLiveData<List<PensionIncomeEntity>> mPensionList =
            new MutableLiveData<>();

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
    }

    public MutableLiveData<PensionIncomeEntity> get() {
        return mPIE;
    }

    public MutableLiveData<PensionIncomeEntity> get(long id) {
        new GetAsyncTask().execute(id);
        return mPIE;
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
            long id = params[0];
            if(id == 0) {
                // need to create default
                return new PensionIncomeEntity(0, RetirementConstants.INCOME_TYPE_PENSION, "", PensionRules.DEFAULT_MIN_AGE, "0");
            } else {
                return mDB.pensionIncomeDao().get(params[0]);
            }
        }

        @Override
        protected void onPostExecute(PensionIncomeEntity pid) {
            mPIE.setValue(pid);
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


    private class UpdateAsyncTask extends AsyncTask<PensionIncomeEntity, Void, PensionIncomeEntity> {

        @Override
        protected PensionIncomeEntity doInBackground(PensionIncomeEntity... params) {
            mDB.pensionIncomeDao().update(params[0]);
            return mDB.pensionIncomeDao().get(params[0].getId());
        }

        @Override
        protected void onPostExecute(PensionIncomeEntity entity) {
            mPIE.setValue(entity);
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
