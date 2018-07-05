package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

/**
 * Created by edm on 6/5/2018.
 */

public interface IncomeDataAccessor {
    IncomeData getIncomeData(AgeData age);
}
