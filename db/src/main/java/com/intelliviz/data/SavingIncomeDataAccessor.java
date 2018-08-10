package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class SavingIncomeDataAccessor implements IncomeDataAccessor {
    private List<IncomeData> mIncomeData;
    Map<Integer, IncomeData> mIncomeDataMap;

    public SavingIncomeDataAccessor(List<IncomeData> incomeData) {
        mIncomeData = incomeData;

        mIncomeDataMap = new HashMap<>();
        for(IncomeData bData : mIncomeData) {
            int month = bData.getAge().getNumberOfMonths();
            mIncomeDataMap.put(month, bData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        return mIncomeDataMap.get(age.getNumberOfMonths());
    }
}
