package com.intelliviz.income.util;


import com.intelliviz.income.data.MilestoneData;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.SummaryEntity;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for database access.
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {

    static void updateSummaryData(AppDatabase db) {
        db.summaryDao().deleteAll();
        List<MilestoneData> milestones = Collections.emptyList(); // TODO implement
        for(MilestoneData msd : milestones) {
            db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
    }
}
