package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;

import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

/**
 * Service for handling database access to tax deferred savings table.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class TaxDeferredIntentService extends IntentService {

    /**
     * Default constructor.
     */
    public TaxDeferredIntentService() {
        super("TaxDeferredIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            long id = intent.getLongExtra(EXTRA_DB_ID, -1);
            if(action == SERVICE_DB_UPDATE) {
                TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(tdid != null) {
                    if(id == -1) {
                        TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATING,
                                RetirementContract.TaxDeferredStatusEntry.ACTION_INSERT, "");
                        String result = TaxDeferredHelper.addTaxDeferredIncome(this, tdid);
                        TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATED,
                                RetirementContract.TaxDeferredStatusEntry.ACTION_INSERT, result);
                    } else {
                        TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATING,
                                RetirementContract.TaxDeferredStatusEntry.ACTION_UPDATE, "");
                        int rowsUpdated = TaxDeferredHelper.saveTaxDeferredData(this, tdid);
                        String rows = Integer.toString(rowsUpdated);
                        TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATED,
                                RetirementContract.TaxDeferredStatusEntry.ACTION_UPDATE, rows);
                    }
                }
            } else if(action == SERVICE_DB_DELETE) {
                TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATING,
                        RetirementContract.TaxDeferredStatusEntry.ACTION_DELETE, "");
                int rowsUpdated = TaxDeferredHelper.deleteTaxDeferredIncome(this, id);
                String rows = Integer.toString(rowsUpdated);
                TaxDeferredHelper.updateStatus(this, RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATED,
                        RetirementContract.TaxDeferredStatusEntry.ACTION_DELETE, rows);
            }
        }
    }
}
