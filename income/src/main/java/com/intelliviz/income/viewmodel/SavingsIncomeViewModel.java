package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.RetirementOptionsEntityRepo;
import com.intelliviz.repo.SavingsIncomeEntityRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeViewModel extends AndroidViewModel {
    private LiveData<SavingsData> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<IncomeDetails>> mIncomeDetails = new MutableLiveData<List<IncomeDetails>>();
    private SavingsIncomeEntityRepo mRepo;
    private RetirementOptionsEntityRepo mRetireRepo;

    public SavingsIncomeViewModel(Application application, long incomeId, int incomeType) {
        super(application);
        mRepo = SavingsIncomeEntityRepo.getInstance(application, incomeType);
        mRetireRepo = RetirementOptionsEntityRepo.getInstance(application);
        //subscribeSavingsIncomeEntityChanges();
        subscribe(incomeId, incomeType);
    }

    private void subscribe(long id, int incomeType) {
        LiveData<SavingsIncomeEntity> entity = mRepo.get(id);
        mSIE = Transformations.switchMap(entity,
                new Function<SavingsIncomeEntity, LiveData<SavingsData>>() {

                    @Override
                    public LiveData<SavingsData> apply(SavingsIncomeEntity input) {
                        MutableLiveData<SavingsData> ldata = new MutableLiveData<>();
                        ldata.setValue(SavingsDataEntityMapper.map(input));
                        return ldata;
                    }
                });
    }

    public MutableLiveData<List<IncomeDetails>> getList() {
        return mIncomeDetails;
    }

    public LiveData<SavingsData> get() {
        return mSIE;
    }

    public void update() {
        //new GetBenefitDataListByIdAsyncTask().execute(mIncomeId);
    }

    public void setData(SavingsData sie) {
        mRepo.setData(SavingsDataEntityMapper.map(sie));
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
            return (T) new SavingsIncomeViewModel(mApplication, mIncomeId, mIncomeType);
        }
    }

    private void subscribeSavingsIncomeEntityChanges() {
        MutableLiveData<SavingsIncomeEntity> sie = mRepo.get();
        mSIE = Transformations.map(sie,
                new Function<SavingsIncomeEntity, SavingsData>() {
                    @Override
                    public SavingsData apply(SavingsIncomeEntity sie) {
                        return SavingsDataEntityMapper.map(sie);
                    }
                });
    }

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<IncomeDetails>> {

        @Override
        protected List<IncomeDetails> doInBackground(Long... params) {
            long id = params[0];
            return getIncomeDetails(id);
        }

        @Override
        protected void onPostExecute(List<IncomeDetails> incomeDetails) {
            mIncomeDetails.setValue(incomeDetails);
        }
    }

    private List<IncomeDetails> getIncomeDetails(long id) {
        RetirementOptions roe = mRetireRepo.get().getValue();
        SavingsIncomeEntity entity = mRepo.get().getValue();
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
}
