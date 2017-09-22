package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;
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
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_GOV_PENSION_RESULT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED_RESULT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;

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
            int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, SERVICE_DB_QUERY);
            long id = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            Intent localIntent;
            GovPensionIncomeData gpid;
            int rowsUpdated;

            switch(action) {
                case INCOME_ACTION_ADD:
                    gpid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    GovPensionHelper.addGovPensionData(this, gpid);
                    localIntent = new Intent(LOCAL_TAX_DEFERRED_RESULT);
                    localIntent.putExtra(EXTRA_DB_RESULT, id);
                    localIntent.putExtra(EXTRA_DB_RESULT_TYPE, EXTRA_DB_RESULT_TYPE_ID);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    break;
                case INCOME_ACTION_EDIT:
                case INCOME_ACTION_UPDATE:
                    gpid = intent.getParcelableExtra(EXTRA_DB_DATA);
                    rowsUpdated = GovPensionHelper.saveGovPensionData(this, gpid);
                    localIntent = new Intent(LOCAL_GOV_PENSION);
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
                        gpid = GovPensionHelper.getGovPensionIncomeData(this, id);
                        RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(this, rod);
                        String birthDate = rod.getBirthdate();
                        AgeData minAge = SystemUtils.parseAgeString(gpid.getMinAge());
                        AgeData maxAge = new AgeData(70, 0);
                        SocialSecurityRules rules = new SocialSecurityRules(birthDate, minAge, maxAge, gpid.getFullMonthlyBenefit());
                        List<MilestoneData> milestones = gpid.getMilestones(ages, rod);
                        gpid.setRules(rules);
                        ArrayList<MilestoneData> listMilestones = new ArrayList<>(milestones);
                        localIntent = new Intent(LOCAL_GOV_PENSION_RESULT);
                        localIntent.putParcelableArrayListExtra(EXTRA_DB_MILESTONES, listMilestones);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                    break;
            }

            DataBaseUtils.updateMilestoneData(this);
        }
    }
}
