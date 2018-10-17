package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.GovPensionEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.List;

public class GovPensionIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private RetirementOptions mRO;
    private long mId;

    public GovPensionIncomeEditViewModel(@NonNull Application application, long id) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mId = id;
        new GetExAsyncTask().execute(id);
    }

    public LiveData<GovPensionViewData> get() {
        return mViewData;
    }

    public void setData(GovPension gp) {
        GovPensionEntity gpe = GovPensionEntityMapper.map(gp);
        if(gpe.getId() == 0) {
            new InsertAsyncTask().execute(gpe);
        } else {
            new UpdateAsyncTask().execute(gpe);
        }
    }

    public AgeData getFRA(GovPension gp) {
        String birthdate;
        if(gp.getOwner() == RetirementConstants.OWNER_PRIMARY) {
            birthdate = mRO.getPrimaryBirthdate();
        } else {
            birthdate = mRO.getSpouseBirthdate();
        }

        int year = AgeUtils.getBirthYear(birthdate);
        return SocialSecurityRules.getFullRetirementAgeFromYear(year);
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
            return (T) new GovPensionIncomeEditViewModel(mApplication, mIncomeId);
        }
    }
}
