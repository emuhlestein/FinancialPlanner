package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.MilestoneAgeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import java.util.List;

import static com.intelliviz.income.util.uiUtils.updateAppWidget;


/**
 * Created by edm on 10/4/2017.
 */

public class MilestoneAgeViewModel extends AndroidViewModel {
    public static final int VALUE_NONE = 0;
    public static final int VALUE_BEFORE = 1;
    public static final int VALUE_DUPLICATE = 2;
    public static final int VALUE_GOOD = 3;
    private AppDatabase mDB;
    private MutableLiveData<List<MilestoneAgeEntity>> mMilestoneAges = new MutableLiveData<>();
    private MutableLiveData<AgeData> mCurrentAge = new MutableLiveData<>();
    private MutableLiveData<Integer> mStatus = new MutableLiveData<>();
    private RetirementOptionsEntity mROM;
    public MilestoneAgeViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetMilestoneAgesAsyncTask().execute();
        new GetRetirementOptionsAsyncTask().execute();
        mStatus.setValue(VALUE_NONE);
    }

    public LiveData<List<MilestoneAgeEntity>> getData() {
        return mMilestoneAges;
    }

    public LiveData<Integer> getStatus() {
        return mStatus;
    }

    public void addAge(AgeData newAge) {
        if(newAge.isBefore(mCurrentAge.getValue())) {
            mStatus.setValue(VALUE_BEFORE);
        } else {
            boolean foundAge = false;
            for(MilestoneAgeEntity msad : mMilestoneAges.getValue()) {
                if(newAge.equals(msad.getAge())) {
                    foundAge = true;
                    break;
                }
            }

            if(foundAge) {
                mStatus.setValue(VALUE_DUPLICATE);
            } else {
                mStatus.setValue(VALUE_GOOD);
                new AddAgeAsyncTask().execute(newAge);
            }
        }
    }

    public void deleteAge(MilestoneAgeEntity age) {
        new DeleteAgeAsyncTask().execute(age);
    }

    private class GetMilestoneAgesAsyncTask extends AsyncTask<Void, Void,  List<MilestoneAgeEntity>> {

        @Override
        protected  List<MilestoneAgeEntity> doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(List<MilestoneAgeEntity> milestoneAges) {
            mMilestoneAges.setValue(milestoneAges);
        }
    }

    private class GetRetirementOptionsAsyncTask extends android.os.AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            mROM = rom;
            mCurrentAge.setValue(AgeUtils.getAge(mROM.getBirthdate()));
        }
    }

    private class AddAgeAsyncTask extends AsyncTask<AgeData, Void,  List<MilestoneAgeEntity>> {

        @Override
        protected List<MilestoneAgeEntity> doInBackground(AgeData... ageDatas) {
            return addNewAge(ageDatas[0]);
        }

        @Override
        protected void onPostExecute(List<MilestoneAgeEntity> milestoneAges) {
            mMilestoneAges.setValue(milestoneAges);
        }
    }

    private class DeleteAgeAsyncTask extends AsyncTask<MilestoneAgeEntity, Void,  List<MilestoneAgeEntity>> {

        @Override
        protected List<MilestoneAgeEntity> doInBackground(MilestoneAgeEntity... ageDatas) {
            return deleteTheAge(ageDatas[0]);
        }

        @Override
        protected void onPostExecute(List<MilestoneAgeEntity> milestoneAges) {
            mMilestoneAges.setValue(milestoneAges);
        }
    }

    private List<MilestoneAgeEntity> addNewAge(AgeData ageData) {
        MilestoneAgeEntity milestoneAgeEntity = new MilestoneAgeEntity(0, ageData);
        mDB.milestoneAgeDao().insert(milestoneAgeEntity);
        updateAppWidget(getApplication());
        return null;
    }

    private List<MilestoneAgeEntity> deleteTheAge(MilestoneAgeEntity age) {
        mDB.milestoneAgeDao().delete(age);
        updateAppWidget(getApplication());
        return null;
    }
}
