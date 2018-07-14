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
        new GetListAsyncTask().execute();
    }

    public MutableLiveData<GovPensionEntity> get(long id) {
        new GetAsyncTask().execute(id);
        return mGPE;
    }

    public LiveData<GovPensionEx> getEx() {
        new GetExAsyncTask().execute();
        return mGpeEx;
    }

    public LiveData<List<GovPensionEntity>> getList() {
        return mGpeList;
    }

    public List<GovPensionEntity> getImmediate() {
        return mDB.govPensionDao().get();
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
            /*
            if(gpeList == null || gpeList.isEmpty()) {
                return null;
            }


            List<GovPension> gpList = new ArrayList<>();
            gpList.add(GovPensionEntityMapper.map(gpeList.get(0)));
            if(gpList.size() > 1) {
                gpList.add(GovPensionEntityMapper.map(gpeList.get(1)));
            }
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpList, roe);
            if(gpeList.size() == 1) {
                return gpList.get(0);
            } else {
                if(gpeList.get(0).getId() == params[0]) {
                    return gpList.get(0);
                } else {
                    return gpList.get(1);
                }
            }
            */
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

    private class DeleteAsyncTask extends AsyncTask<GovPensionEntity, Void, Void> {

        @Override
        protected Void doInBackground(GovPensionEntity... params) {
            mDB.govPensionDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }


/*
    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, Void> {

        @Override
        protected Void doInBackground(GovPensionEntity... params) {
            GovPensionEntity entity = params[0];
            mDB.govPensionDao().update(entity);
            return null;
            */
/*
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            return gpeList;
*/
       // }
/*
        @Override
        protected void onPostExecute(List<GovPensionEntity> gpeList) {

            if(gpeList.size() == 1) {
                mGPE.setValue(gpeList.get(0));
                List<IncomeData> benefitDataList = gpeList.get(0).getIncomeData();
                if(benefitDataList != null) {
                    mBenefitDataList.setValue(getIncomeDetails(gpeList.get(0)));
                }
            } else if(gpeList.size() == 2) {
                GovPensionEntity gpe;
                if(gpeList.get(0).getId() == mIncomeId) {
                    gpe = gpeList.get(0);
                } else {
                    gpe = gpeList.get(1);
                }


                List<IncomeData> benefitDataList = gpe.getIncomeData();
                if(benefitDataList != null) {
                    mBenefitDataList.setValue(getIncomeDetails(gpe));
                }
                mGPE.setValue(gpe);
            }
        }
*/
    //}
}
