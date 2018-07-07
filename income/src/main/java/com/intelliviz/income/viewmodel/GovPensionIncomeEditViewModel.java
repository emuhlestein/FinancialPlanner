package com.intelliviz.income.viewmodel;


import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPension;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.repo.GovEntityRepo;
import com.intelliviz.repo.RetirementOptionsEntityRepo;


/**
 * NOTE: Since the view model last for the entire duration of the app, the async tasks
 * don't need to be static nested classes, they can be inner classes.
 *
 * Created by edm on 9/26/2017.
 */

public class GovPensionIncomeEditViewModel extends AndroidViewModel {

    private LiveData<LiveDataWrapper> mGP;
    private RetirementOptionsEntity mROE;
    private GovEntityRepo mRepo;
    private RetirementOptionsEntityRepo mROERepo;
    private long mIncomeId;

    public GovPensionIncomeEditViewModel(Application application, long incomeId) {
        super(application);
        mRepo = new GovEntityRepo(application, incomeId);
        mROERepo = RetirementOptionsEntityRepo.getInstance(application);
        mIncomeId = incomeId;
        subscribeToGovPensionEntityChanges();
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

    public LiveData<LiveDataWrapper> get() {
        return mGP;
    }

    public void setData(GovPension gp) {
        mRepo.setData(GovPensionEntityMapper.map(gp));
    }
//
//    public void updateSpouseBirthdate(String birthdate) {
//        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
//    }

    public void delete(GovPension gp) {
        mRepo.delete(GovPensionEntityMapper.map(gp));
    }

    private void subscribeToGovPensionEntityChanges() {
        MutableLiveData<GovPensionEntity> gpe = mRepo.get();
        mGP = Transformations.map(gpe,
                new Function<GovPensionEntity, LiveDataWrapper>() {
                    @Override
                    public LiveDataWrapper apply(GovPensionEntity gpe) {
                        return new LiveDataWrapper(null);
                    }
                });
    }

//    private class UpdateSpouseBirthdateAsyncTask extends android.os.AsyncTask<String, Void, RetirementOptionsEntity> {
//
//        @Override
//        protected RetirementOptionsEntity doInBackground(String... params) {
//            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
//            roe.setIncludeSpouse(1);
//            roe.setSpouseBirthdate(params[0]);
//            mDB.retirementOptionsDao().update(roe);
//            return roe;
//        }
//
//        @Override
//        protected void onPostExecute(RetirementOptionsEntity roe) {
//            GovPensionEntity gpe = (GovPensionEntity) mGPE.getValue().getObj();
//            if(gpe == null) {
//                int year = AgeUtils.getBirthYear(roe.getSpouseBirthdate());
//                AgeData age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
//                gpe = new GovPensionEntity(0, RetirementConstants.INCOME_TYPE_GOV_PENSION,
//                        "", "0", age, 1);
//            }
//            gpe.setRules(new SocialSecurityRules(roe.getEndAge(), roe.getSpouseBirthdate()));
//            mGPE.setValue(new LiveDataWrapper(gpe, 0, ""));
//        }
//    }
}
