package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.PensionHelper;

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
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PENSION_RESULT;

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
            int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
            PensionIncomeData pid;

            long id = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            Intent localIntent;
            int rowsUpdated;
            switch (action) {
                case INCOME_ACTION_ADD:
                    pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    id = PensionHelper.addData(this, pid);
                    localIntent = new Intent(LOCAL_PENSION_RESULT);
                    localIntent.putExtra(EXTRA_DB_RESULT, id);
                    localIntent.putExtra(EXTRA_DB_RESULT_TYPE, EXTRA_DB_RESULT_TYPE_ID);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    break;
                case INCOME_ACTION_EDIT:
                case INCOME_ACTION_UPDATE:
                    pid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    rowsUpdated = PensionHelper.saveData(this, pid);
                    localIntent = new Intent(LOCAL_PENSION_RESULT);
                    localIntent.putExtra(EXTRA_DB_RESULT, rowsUpdated);
                    localIntent.putExtra(EXTRA_DB_RESULT_TYPE, EXTRA_DB_RESULT_TYPE_NUM_ROWS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    break;
                case INCOME_ACTION_DELETE:
                    if (id != -1) {
                        rowsUpdated = PensionHelper.deleteData(this, id);
                        String rows = Integer.toString(rowsUpdated);
                    }
                case INCOME_ACTION_VIEW:
                    if (id != -1) {
                        pid = PensionHelper.getData(this, id);
                        RetirementOptionsData rod = null; //RetirementOptionsHelper.getRetirementOptionsData(this);
                        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(this, rod);
                        List<MilestoneData> milestones = pid.getMilestones(ages, rod);
                        ArrayList<MilestoneData> listMilestones = new ArrayList<>(milestones);
                        localIntent = new Intent(LOCAL_PENSION);
                        localIntent.putParcelableArrayListExtra(EXTRA_DB_MILESTONES, listMilestones);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                    break;
            }
        }

        DataBaseUtils.updateMilestoneData(this);
    }
}
