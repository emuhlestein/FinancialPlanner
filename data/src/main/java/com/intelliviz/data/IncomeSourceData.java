package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

public interface IncomeSourceData {
    int getOwner();
    IncomeData getIncomeData(AgeData age);
    double getMonthlyAmount(AgeData age);
    double getBalance(AgeData age);
}
