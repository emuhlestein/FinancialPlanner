package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;


public class RetirementOptionsService extends IntentService {

    public RetirementOptionsService() {
        super("RetirementOptionsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            if(action == SERVICE_DB_QUERY) {
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (rod != null) {
                    Intent localIntent = new Intent(LOCAL_RETIRE_OPTIONS);
                    localIntent.putExtra(EXTRA_DB_DATA, rod);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(rod != null) {
                    int rowsUpdated = RetirementOptionsHelper.saveRetirementOptions(this, rod);
                    Intent localIntent = new Intent(LOCAL_RETIRE_OPTIONS);
                    localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                    localIntent.putExtra(EXTRA_DB_DATA, rod);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        }
    }
}
