package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

/**
 * Created by edm on 6/5/2018.
 */

public interface IncomeDataAccessor {
    /**
     * Get the income data for the specified age of the principle (self).
     * @param age The age.
     * @return The IncomeData.
     */
    IncomeData getIncomeData(AgeData age);
}
