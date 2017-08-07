package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;

import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SavingsIncomeHelper;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_DELETE;
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
            if(action == SERVICE_DB_UPDATE) {
                SavingsIncomeData sid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(sid != null) {
                    if(id == -1) {
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, "", INCOME_TYPE_SAVINGS);
                        String result = SavingsIncomeHelper.addData(this, sid);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_INSERT, result, INCOME_TYPE_SAVINGS);
                    } else {
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, "", INCOME_TYPE_SAVINGS);
                        int rowsUpdated = SavingsIncomeHelper.saveData(this, sid);
                        String rows = Integer.toString(rowsUpdated);
                        DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                                RetirementContract.TransactionStatusEntry.ACTION_UPDATE, rows, INCOME_TYPE_SAVINGS);
                    }
                }
            } else if(action == SERVICE_DB_DELETE) {
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, "", INCOME_TYPE_SAVINGS);
                int rowsUpdated = SavingsIncomeHelper.deleteSavingsIncome(this, id);
                String rows = Integer.toString(rowsUpdated);
                DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                        RetirementContract.TransactionStatusEntry.ACTION_DELETE, rows, INCOME_TYPE_SAVINGS);
            }

            DataBaseUtils.updateMilestoneData(this);
        }
    }
}
