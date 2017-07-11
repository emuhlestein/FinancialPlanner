package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.PensionHelper;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

/**
 * Service for handling database access to pension table.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class PensionDataService extends IntentService {

    /**
     * Constructor.
     */
    public PensionDataService() {
        super("PensionDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            long id = intent.getLongExtra(EXTRA_DB_ID, -1);
            if(action == SERVICE_DB_QUERY) {
                if(id == -1) {
                    return; // error
                }
                PensionIncomeData tdid = PensionHelper.getPensionIncomeData(this, id);
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (tdid != null && rod != null) {
                    Intent localIntent = new Intent(LOCAL_PENSION);
                    localIntent.putExtra(EXTRA_DB_DATA, tdid);
                    localIntent.putExtra(EXTRA_DB_EXTRA_DATA, rod);
                    localIntent.putExtra(EXTRA_DB_ACTION, action);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                PensionIncomeData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(pid != null) {
                    if(id == -1) {
                        PensionHelper.addPensionData(this, pid);
                    } else {
                        int rowsUpdated = PensionHelper.savePensionData(this, pid);
                        Intent localIntent = new Intent(LOCAL_PENSION);
                        localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                        localIntent.putExtra(EXTRA_DB_ACTION, action);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                }
            }
        }
    }
}
