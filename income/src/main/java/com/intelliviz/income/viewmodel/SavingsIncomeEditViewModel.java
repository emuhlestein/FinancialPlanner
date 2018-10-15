package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsDataEx;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.income.data.SavingsViewData;

import java.util.List;

public class SavingsIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<SavingsViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;
    private int mIncomeType;

    public SavingsIncomeEditViewModel(@NonNull Application application, long id, int incomeType) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mId = id;
        mIncomeType = incomeType;
        new GetExAsyncTask().execute(id);
    }

    public LiveData<SavingsViewData> get() {
        return mViewData;
    }

    public void setData(SavingsData sd) {
        SavingsIncomeEntity sie = SavingsDataEntityMapper.map(sd);
        if(sie.getId() == 0) {
            new InsertAsyncTask().execute(sie);
        } else {
            new UpdateAsyncTask().execute(sie);
        }
    }

    public void update() {
        new GetExAsyncTask().execute();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;
        private int mIncomeType;

        public Factory(@NonNull Application application, long incomeId, int incomeType) {
            mApplication = application;
            mIncomeId = incomeId;
            mIncomeType = incomeType;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SavingsIncomeEditViewModel(mApplication, mIncomeId, mIncomeType);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            SavingsIncomeEntity sie = params[0];
            return mDB.savingsIncomeDao().update(sie);
        }
    }

    private class InsertAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().insert(params[0]);
        }
    }

    private class GetExAsyncTask extends AsyncTask<Long, Void, SavingsDataEx> {
        @Override
        protected SavingsDataEx doInBackground(Long... params) {
            SavingsIncomeEntity sie = mDB.savingsIncomeDao().get(params[0]);
            List<SavingsIncomeEntity> sieList = mDB.savingsIncomeDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new SavingsDataEx(sie, sieList.size(), roe);
        }

        @Override
        protected void onPostExecute(SavingsDataEx sdEx) {
            RetirementOptions ro = RetirementOptionsMapper.map(sdEx.getROE());
            SavingsData sd = null;
            if(sdEx.getSie() != null) {
                sd = SavingsDataEntityMapper.map(sdEx.getSie());
            }

            SavingsIncomeHelper helper = new SavingsIncomeHelper(sd, ro, sdEx.getNumRecords());
            long id = 0;
            int type = mIncomeType;
            if(sdEx.getSie() != null) {
                id = sdEx.getSie().getId();
                type = sdEx.getSie().getType();
            }
            mViewData.setValue(helper.get(id, type));
        }
    }
}
