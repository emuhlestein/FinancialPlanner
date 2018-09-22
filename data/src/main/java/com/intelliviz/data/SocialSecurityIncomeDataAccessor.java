package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class SocialSecurityIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private Map<Integer, IncomeData> mIncomeDataMap;

    public SocialSecurityIncomeDataAccessor(List<IncomeData> incomeData, int owner) {
        super(owner);
        mIncomeDataMap = new HashMap<>();
        for(IncomeData bData : incomeData) {
            int month = bData.getAge().getNumberOfMonths();
            mIncomeDataMap.put(month, bData);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        return mIncomeDataMap.get(age.getNumberOfMonths());
    }
}
