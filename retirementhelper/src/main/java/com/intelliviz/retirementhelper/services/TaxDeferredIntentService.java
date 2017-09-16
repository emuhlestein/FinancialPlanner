package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_MILESTONES;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;

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
            int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
            long id = intent.getLongExtra(EXTRA_DB_ID, -1);
            switch(action) {
                case INCOME_ACTION_EDIT:
                    TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    if (tdid != null) {
                        if (id == -1) {
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
                    break;
                case INCOME_ACTION_DELETE:
                    DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                            RetirementContract.TransactionStatusEntry.ACTION_DELETE, "", INCOME_TYPE_TAX_DEFERRED);
                    int rowsUpdated = TaxDeferredHelper.deleteTaxDeferredIncome(this, id);
                    String rows = Integer.toString(rowsUpdated);
                    DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                            RetirementContract.TransactionStatusEntry.ACTION_DELETE, rows, INCOME_TYPE_TAX_DEFERRED);
                    break;
                case INCOME_ACTION_VIEW:
                    if(id != -1) {
                        TaxDeferredIncomeData tid = TaxDeferredHelper.getTaxDeferredIncomeData(this, id);
                        RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(this, rod);
                        List<MilestoneData> milestones = tid.getMilestones(this, ages, rod);
                        ArrayList<MilestoneData> listMilestones = new ArrayList<>(milestones);
                        Intent localIntent = new Intent(LOCAL_TAX_DEFERRED);
                        localIntent.putParcelableArrayListExtra(EXTRA_DB_MILESTONES, listMilestones);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                    break;
                }

            }

            DataBaseUtils.updateMilestoneData(this);
        }
}
