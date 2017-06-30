package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import java.util.ArrayList;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MILESTONEAGE_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_MILESTONE_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

public class MilestoneAgeIntentService extends IntentService {

    public MilestoneAgeIntentService() {
        super("MilestoneAgeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            if(action == SERVICE_DB_QUERY) {
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                ArrayList<MilestoneAgeData> milestoneAges = DataBaseUtils.getMilestoneAges(this, rod);
                Intent localIntent = new Intent(LOCAL_MILESTONE_AGE);
                localIntent.putParcelableArrayListExtra(EXTRA_MILESTONEAGE_DATA, milestoneAges);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } else if(action == SERVICE_DB_UPDATE) {

            }
        }
    }
}
