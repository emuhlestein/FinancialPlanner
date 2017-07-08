package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SummaryData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling database access to summarydata table.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class SummaryDataIntentService extends IntentService {

    /**
     * Default constructor.
     */
    public SummaryDataIntentService() {
        super("SummaryDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
            List<MilestoneData> milestones = BenefitHelper.getAllMilestones(this, rod);
            List<SummaryData> listSummaryData = new ArrayList<>();
            for(MilestoneData msd : milestones) {
                listSummaryData.add(new SummaryData(msd.getStartAge().toString(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
            }
            Uri uri = RetirementContract.SummaryEntry.CONTENT_URI;
            getContentResolver().delete(uri, null, null);
            for(SummaryData summaryData : listSummaryData) {
                ContentValues values = new ContentValues();
                values.put(RetirementContract.SummaryEntry.COLUMN_AGE, summaryData.getAge());
                values.put(RetirementContract.SummaryEntry.COLUMN_AMOUNT, summaryData.getMonthlyBenefit());
                getContentResolver().insert(RetirementContract.SummaryEntry.CONTENT_URI, values);
            }
        }
    }
}
