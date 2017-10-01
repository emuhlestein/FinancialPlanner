package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SummaryData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.db.RetirementOptionsDatabase;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getMilestoneAges;

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
            RetirementOptionsData rod = RetirementOptionsDatabase.getInstance(this).get();
            List<MilestoneAgeData> ages = getMilestoneAges(this, rod);
            List<MilestoneData> milestones = DataBaseUtils.getAllMilestones(this, ages, rod);
            List<SummaryData> listSummaryData = new ArrayList<>();
            for(MilestoneData msd : milestones) {
                listSummaryData.add(new SummaryData(msd.getStartAge().toString(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
            }

            // delete all summary tables, then readd them.
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
