package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class SavingIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private Map<Integer, IncomeData> mIncomeDataMap;
    private List<IncomeData> mIncomeDataList;
    private RetirementOptions mRO;

    public SavingIncomeDataAccessor(int owner, List<IncomeData> incomeData, RetirementOptions ro) {
        super(owner);
        mRO = ro;
        mIncomeDataList = new ArrayList<>(incomeData);
        mIncomeDataMap = new HashMap<>();
        for(IncomeData bData : incomeData) {
            int numberOfMonths = bData.getAge().getNumberOfMonths();
            mIncomeDataMap.put(numberOfMonths, bData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        age = getOwnerAge(age, mRO);
        IncomeData incomeData = mIncomeDataMap.get(age.getNumberOfMonths());
        if(incomeData == null) {
            return new IncomeData(age, 0d, 0d, 0, null);
        } else {
            return incomeData;
        }
    }
}
