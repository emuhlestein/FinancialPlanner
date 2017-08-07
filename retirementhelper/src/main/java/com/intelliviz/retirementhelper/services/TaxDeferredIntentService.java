package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;

import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
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
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, "", INCOME_TYPE_TAX_DEFERRED);
                        String result = TaxDeferredHelper.addTaxDeferredIncome(this, tdid);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, result, INCOME_TYPE_TAX_DEFERRED);
                    } else {
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, "", INCOME_TYPE_TAX_DEFERRED);
                        int rowsUpdated = TaxDeferredHelper.saveData(this, tdid);
                        String rows = Integer.toString(rowsUpdated);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, rows, INCOME_TYPE_TAX_DEFERRED);
                    }
                }
            } else if(action == SERVICE_DB_DELETE) {
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, "", INCOME_TYPE_TAX_DEFERRED);
                int rowsUpdated = TaxDeferredHelper.deleteTaxDeferredIncome(this, id);
                String rows = Integer.toString(rowsUpdated);
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, rows, INCOME_TYPE_TAX_DEFERRED);
            }

            DataBaseUtils.updateMilestoneData(this);
        }
    }
}
