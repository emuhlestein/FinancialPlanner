package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public interface IncomeSourceData {
    IncomeData getIncomeData();
    IncomeData getIncomeData(AgeData age);
    IncomeData getIncomeData(IncomeData incomeData);
}
