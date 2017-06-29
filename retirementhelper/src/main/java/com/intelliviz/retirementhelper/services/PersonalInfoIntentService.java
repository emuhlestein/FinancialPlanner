package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PERSONAL_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;


public class PersonalInfoIntentService extends IntentService {

    public PersonalInfoIntentService() {
        super("PersonalInfoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            if (action == SERVICE_DB_UPDATE) {
                String birthdate = intent.getStringExtra(EXTRA_DB_DATA);
                if (birthdate != null) {
                    int rowsUpdated = RetirementOptionsHelper.saveBirthdate(this, birthdate);
                    Intent localIntent = new Intent(LOCAL_PERSONAL_DATA);
                    localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        }
    }
}
