package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPensionEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.GovPensionViewData;

import java.util.List;

public class GovPensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public GovPensionIncomeDetailsViewModel(@NonNull Application application, long id) {
        super(application);
        mId = id;
        mDB = AppDatabase.getInstance(application);
        new GetExAsyncTask().execute(id);
    }

    public LiveData<GovPensionViewData> get() {
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
            return (T) new GovPensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetExAsyncTask extends AsyncTask<Long, Void, GovPensionEx> {

        @Override
        protected GovPensionEx doInBackground(Long... params) {
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new GovPensionEx(gpeList, roe);
        }

        @Override
        protected void onPostExecute(GovPensionEx gpeEx) {
            RetirementOptions ro = RetirementOptionsMapper.map(gpeEx.getROE());
            List<GovPensionEntity> gpeList = gpeEx.getGpeList();
            GovPensionHelper helper = new GovPensionHelper(getApplication(), gpeList, ro);
            GovPensionViewData viewData = helper.get(mId);

            mViewData.setValue(viewData);
        }
    }
}
