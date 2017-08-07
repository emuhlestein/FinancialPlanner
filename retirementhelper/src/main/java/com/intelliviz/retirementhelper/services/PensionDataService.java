package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;

import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.PensionHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_DELETE;
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
            if(action == SERVICE_DB_UPDATE) {
                PensionIncomeData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(pid != null) {
                    if(id == -1) {
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, "", INCOME_TYPE_PENSION);
                        String result = PensionHelper.addData(this, pid);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, result, INCOME_TYPE_PENSION);
                    } else {
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, "", INCOME_TYPE_PENSION);
                        int rowsUpdated = PensionHelper.saveData(this, pid);
                        String rows = Integer.toString(rowsUpdated);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, rows, INCOME_TYPE_PENSION);
                    }
                }
            } else if(action == SERVICE_DB_DELETE) {
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, "", INCOME_TYPE_PENSION);
                int rowsUpdated = PensionHelper.deleteData(this, id);
                String rows = Integer.toString(rowsUpdated);
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, rows, INCOME_TYPE_PENSION);
            }

            DataBaseUtils.updateMilestoneData(this);
        }
    }
}
