package com.intelliviz.retirementhelper.ui.income;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.util.GovEntityAccessor;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.LiveDataWrapper;

import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_MAX_NUM_SOCIAL_SECURITY_FREE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_NO_ERROR;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EC_PRINCIPLE_SPOUSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Created by edm on 3/12/2018.
 */

public class StartGovPensionEditActivityTask extends AsyncTask<Void, Void, LiveDataWrapper> {
    private FragmentActivity mActivity;
    private long mId;
    private int mAction;

    public StartGovPensionEditActivityTask(FragmentActivity activity, long id, int action) {
        mActivity = activity;
        mId = id;
        mAction = action;
    }

    @Override
    protected LiveDataWrapper doInBackground(Void... voids) {
        AppDatabase mDB = AppDatabase.getInstance(mActivity);
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        GovEntityAccessor govEntityAccessor = new GovEntityAccessor(gpeList, roe);
        return govEntityAccessor.getEntity(mId);
    }

    @Override
    protected void onPostExecute(LiveDataWrapper liveDataWrapper) {
        tryToStartGovPensionActivity(liveDataWrapper, mId, mAction);
    }

    private void tryToStartGovPensionActivity(LiveDataWrapper liveDataWrapper, long id, int action) {
        int state = liveDataWrapper.getState();
        if(state == EC_NO_ERROR || state == EC_PRINCIPLE_SPOUSE) {
            Intent intent = new Intent(mActivity, GovPensionIncomeEditActivity.class);
            intent.putExtra(EXTRA_INCOME_SOURCE_ID, id);
            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
            mActivity.startActivity(intent);
        } else if(state == EC_MAX_NUM_SOCIAL_SECURITY ||
                state == EC_MAX_NUM_SOCIAL_SECURITY_FREE) {
            String[] messages = mActivity.getResources().getStringArray(R.array.error_codes);
            FragmentManager fm = mActivity.getSupportFragmentManager();
            IncomeSourceListFragment.MyAlertDialog alertDialog = IncomeSourceListFragment.MyAlertDialog.newInstance("Warning", messages[state]);
            alertDialog.show(fm, "fragment_alert");
        } else if(state == EC_NO_SPOUSE_BIRTHDATE) {
            final long spouseId = id;
            final int newAction = action;
            SystemUtils.showDialog(mActivity, "01-01-1900", new BirthdateDialogAction() {
                @Override
                public void onGetBirthdate(String birthdate) {
                    new UpdateSpouseBirthdateAsyncTask(mActivity, spouseId, newAction).execute(birthdate);
                }
            });
        }
    }

    private static class UpdateSpouseBirthdateAsyncTask extends android.os.AsyncTask<String, Void, Void> {
        private AppDatabase mDB;
        private Context mContext;
        private long mId;
        private int mAction;

        public UpdateSpouseBirthdateAsyncTask(Context context, long id, int action) {
            mDB = AppDatabase.getInstance(context.getApplicationContext());
            mContext = context;
            mId = id;
            mAction = action;
        }

        @Override
        protected Void doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(mContext, GovPensionIncomeEditActivity.class);
            intent.putExtra(EXTRA_INCOME_SOURCE_ID, mId);
            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, mAction);
            mContext.startActivity(intent);
        }
    }
}
