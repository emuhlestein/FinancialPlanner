package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SavingsHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

/**
 * Service for handling database access to savings table.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class SavingsDataService extends IntentService {

    /**
     * Default constructor.
     */
    public SavingsDataService() {
        super("SavingsDataService");
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
                SavingsIncomeData sid = SavingsHelper.getSavingsIncomeData(this, id);
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (sid != null && rod != null) {
                    Intent localIntent = new Intent(LOCAL_SAVINGS);
                    localIntent.putExtra(EXTRA_DB_DATA, sid);
                    localIntent.putExtra(EXTRA_DB_EXTRA_DATA, rod);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                SavingsIncomeData sid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(sid != null) {
                    if(id == -1) {
                        SavingsHelper.addSavingsIncome(this, sid);
                    } else {
                        int rowsUpdated = SavingsHelper.saveSavingsIncomeData(this, sid);
                        Intent localIntent = new Intent(LOCAL_SAVINGS);
                        localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                }
            }
        }
    }
}
