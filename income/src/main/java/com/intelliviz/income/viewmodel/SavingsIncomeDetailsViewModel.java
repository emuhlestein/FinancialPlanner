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

public class SavingsIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<SavingsViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public SavingsIncomeDetailsViewModel(@NonNull Application application, long id) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mId = id;
        new GetExAsyncTask().execute(id);
    }

    public LiveData<SavingsViewData> get() {
        return mViewData;
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
            return (T) new SavingsIncomeDetailsViewModel(mApplication, mIncomeId);
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
            long id;
            int type;
            if(sdEx.getSie() != null) {
                id = sdEx.getSie().getId();
                type = sdEx.getSie().getType();
                mViewData.setValue(helper.get(id, type));
            }
        }
    }
}
