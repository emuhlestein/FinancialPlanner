package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class Savings401kIncomeDataAccessor implements IncomeDataAccessor {
    private List<IncomeData> mIncomeData;
    Map<Integer, IncomeData> mIncomeDataMap;

    public Savings401kIncomeDataAccessor(List<IncomeData> incomeData) {
        mIncomeData = incomeData;

        mIncomeDataMap = new HashMap<>();
        for(IncomeData iData : mIncomeData) {
            int year = iData.getAge().getYear();
            mIncomeDataMap.put(year, iData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        return mIncomeDataMap.get(age.getYear());
    }
}
