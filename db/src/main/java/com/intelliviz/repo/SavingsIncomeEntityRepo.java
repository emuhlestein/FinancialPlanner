package com.intelliviz.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.SavingsIncomeEntity;

import java.util.List;

public class SavingsIncomeEntityRepo {
    private volatile static SavingsIncomeEntityRepo mINSTANCE;
    private AppDatabase mDB;
    private int mIncomeType;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<SavingsIncomeEntity>> mSieList = new MutableLiveData<List<SavingsIncomeEntity>>();

    public static SavingsIncomeEntityRepo getInstance(Application application, int incomeType) {
        if(mINSTANCE == null) {
            synchronized (SavingsIncomeEntityRepo.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = new SavingsIncomeEntityRepo(application, incomeType);
                }
            }
        }
        return mINSTANCE;
    }

    private SavingsIncomeEntityRepo(Application application, int incomeType) {
        mDB = AppDatabase.getInstance(application);
        mIncomeType = incomeType;
        new GetListAsyncTask().execute();
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public MutableLiveData<SavingsIncomeEntity> get(long id) {
        new GetAsyncTask().execute(id);
        return mSIE;
    }

    public MutableLiveData<List<SavingsIncomeEntity>> getList() {
        return mSieList;
    }

    public void setData(SavingsIncomeEntity sie) {
        if(sie.getId() == 0) {
            new InsertAsyncTask().execute(sie);
        } else {
            new UpdateAsyncTask().execute(sie);
        }
    }

    public void delete(SavingsIncomeEntity entity) {
        new DeleteAsyncTask().execute(entity);
    }

    public void update() {
        new GetListAsyncTask().execute();
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected SavingsIncomeEntity doInBackground(Long... params) {
            long id = params[0];
            if(id == 0) {
                return new SavingsIncomeEntity(0, mIncomeType);
            } else {
                return mDB.savingsIncomeDao().get(params[0]);
            }
        }

        @Override
        protected void onPostExecute(SavingsIncomeEntity tdid) {
            mSIE.setValue(tdid);
        }
    }

    private class GetListAsyncTask extends AsyncTask<Void, Void, List<SavingsIncomeEntity>> {

        @Override
        protected List<SavingsIncomeEntity> doInBackground(Void... params) {
           return mDB.savingsIncomeDao().get();
        }

        @Override
        protected void onPostExecute(List<SavingsIncomeEntity> sie) {
            mSieList.setValue(sie);
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

/*
    private List<IncomeDetails> getIncomeDetails(long id) {
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        SavingsIncomeEntity entity = mDB.savingsIncomeDao().get(id);
        String birthdate = roe.getBirthdate();
        AgeData endAge = roe.getEndAge();
        if(entity.getType() == RetirementConstants.INCOME_TYPE_401K) {
            Savings401kIncomeRules s4ir = new Savings401kIncomeRules(birthdate, endAge);
            entity.setRules(s4ir);
        } else {
            SavingsIncomeRules sir = new SavingsIncomeRules(birthdate, endAge);
            entity.setRules(sir);

        }

        AgeData startAge = AgeUtils.getAge(roe.getBirthdate());
        endAge = roe.getEndAge();
        IncomeDataAccessor accessor = entity.getIncomeDataAccessor();
        List<IncomeDetails> incomeDetails = new ArrayList<>();
        for(int year = startAge.getYear(); year <= endAge.getYear(); year++) {
            AgeData age = new AgeData(year, 0);
            IncomeData benefitData = accessor.getIncomeData(age);
            String line1;
            int status;
            String balance;
            String amount;
            if(benefitData == null) {
                balance = "0.0";
                amount = "0.0";
                status = 0;
            } else {
                balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
                amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
                status = benefitData.getBalanceState();
                if (benefitData.isPenalty()) {
                    //status = 0;
                }
            }
            line1 = age.toString() + "   " + amount + "  " + balance;
            IncomeDetails incomeDetail = new IncomeDetails(line1, status, "");
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
*/
}
