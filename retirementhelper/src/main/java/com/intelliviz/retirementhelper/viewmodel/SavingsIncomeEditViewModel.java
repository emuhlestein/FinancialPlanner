package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_UNKNOWN;

/**
 * Created by edm on 9/30/2017.
 */

public class SavingsIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<RetirementOptionsEntity> mROE =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public SavingsIncomeEditViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<SavingsIncomeEntity> getData() {
        return mSIE;
    }

    public void setData(SavingsIncomeEntity tdid) {
        mSIE.setValue(tdid);
        if(tdid.getId() == 0) {
            new InsertAsyncTask().execute(tdid);
        } else {
            new UpdateAsyncTask().execute(tdid);
        }
    }

    public void delete(SavingsIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
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
            return (T) new SavingsIncomeEditViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected SavingsIncomeEntity doInBackground(Long... params) {
            long id = params[0];
            if(id == 0) {
                // need to create default
                RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
                String birthdate = roe.getBirthdate();
                AgeData startAge = SystemUtils.getAge(birthdate);
                return new SavingsIncomeEntity(id, INCOME_TYPE_UNKNOWN,
                        "", "0", "0", "0", startAge, "0", "0");
            } else {
                return mDB.savingsIncomeDao().get(id);
            }
        }

        @Override
        protected void onPostExecute(SavingsIncomeEntity tdid) {
            mSIE.setValue(tdid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Long> {

        @Override
        protected Long doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            mId = id;
        }
    }

    private class DeleteAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            mDB.savingsIncomeDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Integer numRowsInserted) {
        }
    }
}
