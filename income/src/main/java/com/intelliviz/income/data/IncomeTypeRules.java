package com.intelliviz.income.data;

import android.os.Bundle;

import java.util.List;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    void setValues(Bundle bundle);
    List<IncomeData> getIncomeData();
    IncomeData getIncomeData(IncomeData incomeData);
    IncomeDataAccessor getIncomeDataAccessor();
}
