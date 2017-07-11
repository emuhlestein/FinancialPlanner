package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

/**
 * Service for handling database access to governemnt pension table.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class GovPensionDataService extends IntentService {

    /**
     * Default constructor.
     */
    public GovPensionDataService() {
        super("GovPensionDataService");
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
                GovPensionIncomeData gpid = GovPensionHelper.getGovPensionIncomeData(this, id);
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (gpid != null && rod != null) {
                    Intent localIntent = new Intent(LOCAL_GOV_PENSION);
                    localIntent.putExtra(EXTRA_DB_DATA, gpid);
                    localIntent.putExtra(EXTRA_DB_EXTRA_DATA, rod);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                GovPensionIncomeData gpid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(gpid != null) {
                    if(id == -1) {
                        GovPensionHelper.addGovPensionData(this, gpid);
                    } else {
                        int rowsUpdated = GovPensionHelper.saveGovPensionData(this, gpid);
                        Intent localIntent = new Intent(LOCAL_GOV_PENSION);
                        localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                }
            }
        }
    }
}
