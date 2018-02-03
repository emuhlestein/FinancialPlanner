package com.intelliviz.retirementhelper.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

import java.util.List;

/**
 * NOTE: Since the view model last for the entire duration of the app, the async tasks
 * don't need to be static nested classes, they can be inner classes.
 *
 * Created by edm on 9/26/2017.
 */

public class GovPensionIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<List<GovPensionEntity>> mListGPE =
            new MutableLiveData<>();
    private RetirementOptionsEntity mROE;
    private AppDatabase mDB;

    public GovPensionIncomeEditViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        buildList();
    }

    public LiveData<List<GovPensionEntity>> getList() {
        return mListGPE;
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

    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
            buildList();
        }
    }

    private class InsertAsyncTask extends AsyncTask<GovPensionEntity, Void, Long> {

        @Override
        protected Long doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
            buildList();
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
            buildList();
        }
    }

    private void buildList() {
        new AsyncTask<Void, Void, List<GovPensionEntity>>() {

            @Override
            protected List<GovPensionEntity> doInBackground(Void... voids) {
                mROE = mDB.retirementOptionsDao().get();
                List<GovPensionEntity> govPensionList = mDB.govPensionDao().get();
                SocialSecurityRules.setRulesOnGovPensionEntities(govPensionList, mROE);

                return govPensionList;
            }

            @Override
            protected void onPostExecute(List<GovPensionEntity> govPensionEntities) {
                if(govPensionEntities != null) {
                    mListGPE.setValue(govPensionEntities);
                }
            }
        }.execute();
    }
}
