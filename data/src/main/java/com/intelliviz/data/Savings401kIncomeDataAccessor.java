package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class Savings401kIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private List<IncomeData> mIncomeData;
    Map<Integer, IncomeData> mIncomeDataMap;
    private RetirementOptions mRO;

    public Savings401kIncomeDataAccessor(int owner, List<IncomeData> incomeData,RetirementOptions ro) {
        super(owner);
        mIncomeData = incomeData;
        mRO = ro;

        mIncomeDataMap = new HashMap<>();
        for(IncomeData iData : mIncomeData) {
            int year = iData.getAge().getYear();
            mIncomeDataMap.put(year, iData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        age = getOwnerAge(age, mRO);
        return mIncomeDataMap.get(age.getYear());
    }
}
