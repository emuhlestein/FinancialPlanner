package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class SavingIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private Map<Integer, IncomeData> mIncomeDataMap;
    private RetirementOptions mRO;

    public SavingIncomeDataAccessor(int owner, List<IncomeData> incomeData, RetirementOptions ro) {
        super(owner);
        mRO = ro;
        mIncomeDataMap = new HashMap<>();
        for(IncomeData bData : incomeData) {
            int month = bData.getAge().getNumberOfMonths();
            mIncomeDataMap.put(month, bData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        age = getOwnerAge(age, mRO);
        return mIncomeDataMap.get(age.getNumberOfMonths());
    }
}
