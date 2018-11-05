package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public interface IncomeSourceData {
    IncomeData getIncomeData(AgeData age);
}
