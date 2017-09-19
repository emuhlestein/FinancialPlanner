package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_MILESTONES;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_RESULT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_RESULT_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_RESULT_TYPE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_RESULT_TYPE_NUM_ROWS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_ADD;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_UPDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED_RESULT;

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
            TaxDeferredIncomeData tdid;

            long id = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            Intent localIntent;
            int rowsUpdated;
            switch(action) {
                case INCOME_ACTION_ADD:
                    tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    id = TaxDeferredHelper.addTaxDeferredIncome(this, tdid);
                    localIntent = new Intent(LOCAL_TAX_DEFERRED_RESULT);
                    localIntent.putExtra(EXTRA_DB_RESULT, id);
                    localIntent.putExtra(EXTRA_DB_RESULT_TYPE, EXTRA_DB_RESULT_TYPE_ID);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    break;
                case INCOME_ACTION_EDIT:
                case INCOME_ACTION_UPDATE:
                    tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    rowsUpdated = TaxDeferredHelper.saveData(this, tdid);
                    localIntent = new Intent(LOCAL_TAX_DEFERRED_RESULT);
                    localIntent.putExtra(EXTRA_DB_RESULT, rowsUpdated);
                    localIntent.putExtra(EXTRA_DB_RESULT_TYPE, EXTRA_DB_RESULT_TYPE_NUM_ROWS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    break;
                case INCOME_ACTION_DELETE:
                    if(id != -1) {
                        rowsUpdated = TaxDeferredHelper.deleteTaxDeferredIncome(this, id);
                        String rows = Integer.toString(rowsUpdated);
                    }
                    break;
                case INCOME_ACTION_VIEW:
                    if(id != -1) {
                        tdid = TaxDeferredHelper.getTaxDeferredIncomeData(this, id);
                        RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(this, rod);
                        List<MilestoneData> milestones = tdid.getMilestones(this, ages, rod);
                        ArrayList<MilestoneData> listMilestones = new ArrayList<>(milestones);
                        localIntent = new Intent(LOCAL_TAX_DEFERRED);
                        localIntent.putParcelableArrayListExtra(EXTRA_DB_MILESTONES, listMilestones);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                    break;
                }

            }

            DataBaseUtils.updateMilestoneData(this);
        }
}
