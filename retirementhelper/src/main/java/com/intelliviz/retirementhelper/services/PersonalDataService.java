package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_PERID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROD;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SERVICE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PERSONAL_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_ACTION_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_ACTION_UPDATE;

public class PersonalDataService extends IntentService {

    public PersonalDataService() {
        super("PersonalDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_SERVICE_ACTION, SERVICE_ACTION_QUERY);
            if(action == SERVICE_ACTION_QUERY) {
                PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(this);
                Intent localIntent = new Intent(LOCAL_PERSONAL_DATA);
                boolean broadcast = false;
                if (pid != null) {
                    localIntent.putExtra(EXTRA_DB_PERID, pid);
                    broadcast = true;
                }
                RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(this);
                if (rod != null) {
                    localIntent.putExtra(EXTRA_DB_ROD, rod);
                    broadcast = true;
                }

                if(broadcast) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_ACTION_UPDATE) {
                PersonalInfoData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(pid != null) {
                    int rowsUpdated = DataBaseUtils.savePersonalInfo(this, pid);
                    Intent localIntent = new Intent(LOCAL_PERSONAL_DATA);
                    localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                    localIntent.putExtra(EXTRA_DB_DATA, pid);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        }
    }
}
