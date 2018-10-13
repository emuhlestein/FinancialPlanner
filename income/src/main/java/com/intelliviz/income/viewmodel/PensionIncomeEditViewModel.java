package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionDataEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.dao.PensionIncomeDaoHelper;
import com.intelliviz.db.dao.RetirementOptionsDaoHelper;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.PensionViewData;

import java.util.List;

public class PensionIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<PensionViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public PensionIncomeEditViewModel(@NonNull Application application, long id) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mId = id;
        new GetExAsyncTask().execute(id);
    }

    public LiveData<PensionViewData> get() {
        return mViewData;
    }

    public void setData(PensionData pd) {

        PensionIncomeEntity pie = PensionDataEntityMapper.map(pd);
        if(pie.getId() == 0) {
            new InsertAsyncTask().execute(pie);
        } else {
            new UpdateAsyncTask().execute(pie);
        }
    }

    public void update() {
        new GetExAsyncTask().execute(mId);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;

        public Factory(@NonNull Application application, long incomeId) {
            mApplication = application;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new PensionIncomeEditViewModel(mApplication, mIncomeId);
        }
    }

    private class GetExAsyncTask extends AsyncTask<Long, Void, PensionDataEx> {

        @Override
        protected PensionDataEx doInBackground(Long... params) {
            PensionIncomeEntity pie = PensionIncomeDaoHelper.getPensionIncomeEntity(mDB, params[0]);
            List<PensionIncomeEntity> pieList = PensionIncomeDaoHelper.getAllPensionIncomeEntities(mDB);
            RetirementOptionsEntity roe = RetirementOptionsDaoHelper.get(mDB);
            return new PensionDataEx(pie, pieList.size(), roe);
        }

        @Override
        protected void onPostExecute(PensionDataEx pdEx) {
            RetirementOptions ro = RetirementOptionsMapper.map(pdEx.getROE());
            PensionData pd = null;
            if(pdEx.getPie() != null) {
                pd = PensionDataEntityMapper.map(pdEx.getPie());
            }
            PensionIncomeHelper helper = new PensionIncomeHelper(pd, ro, pdEx.getNumRecords());
            long id = 0;
            if(pdEx.getPie() != null) {
                id = pdEx.getPie().getId();
            }
            mViewData.setValue(helper.get(id));
        }
    }

    private class UpdateAsyncTask extends AsyncTask<PensionIncomeEntity, Void, PensionDataEx> {

        @Override
        protected PensionDataEx doInBackground(PensionIncomeEntity... params) {
            PensionIncomeDaoHelper.update(mDB, params[0]);
            PensionIncomeEntity pie = PensionIncomeDaoHelper.getPensionIncomeEntity(mDB, params[0].getId());
            List<PensionIncomeEntity> pieList = PensionIncomeDaoHelper.getAllPensionIncomeEntities(mDB);
            RetirementOptionsEntity roe = RetirementOptionsDaoHelper.get(mDB);
            return new PensionDataEx(pie, pieList.size(), roe);
        }

        @Override
        protected void onPostExecute(PensionDataEx pdEx) {
            RetirementOptions ro = RetirementOptionsMapper.map(pdEx.getROE());
            PensionData pd = null;
            if(pdEx.getPie() != null) {
                pd = PensionDataEntityMapper.map(pdEx.getPie());
            }
            PensionIncomeHelper helper = new PensionIncomeHelper(pd, ro, pdEx.getNumRecords());
            long id = 0;
            if(pdEx.getPie() != null) {
                id = pdEx.getPie().getId();
            }
            mViewData.setValue(helper.get(id));
        }
    }

    private class InsertAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(PensionIncomeEntity... params) {
            return PensionIncomeDaoHelper.insert(mDB, params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Void> {

        @Override
        protected Void doInBackground(PensionIncomeEntity... params) {
            PensionIncomeDaoHelper.delete(mDB, params[0]);
            return null;
        }
    }
}
