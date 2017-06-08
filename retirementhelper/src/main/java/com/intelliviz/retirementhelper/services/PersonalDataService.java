package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.PersonalInfoData;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PERSONAL_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

public class PersonalDataService extends IntentService {

    public PersonalDataService() {
        super("PersonalDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            if(action == SERVICE_DB_QUERY) {
                PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(this);
                if (pid != null) {
                    Intent localIntent = new Intent(LOCAL_RETIRE_OPTIONS);
                    localIntent.putExtra(EXTRA_DB_DATA, pid);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                PersonalInfoData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(pid != null) {
                    int rowsUpdated = DataBaseUtils.savePersonalInfo(this, pid);
                    Intent localIntent = new Intent(LOCAL_PERSONAL_DATA);
                    localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        }
    }
}
