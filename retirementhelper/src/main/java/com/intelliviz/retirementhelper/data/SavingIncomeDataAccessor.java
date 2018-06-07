package com.intelliviz.retirementhelper.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 6/5/2018.
 */

public class SavingIncomeDataAccessor implements IncomeDataAccessor {
    private List<BenefitData> mBenefitData;
    Map<Integer, BenefitData> mBenefitDataMap;

    public SavingIncomeDataAccessor(List<BenefitData> benefitData) {
        mBenefitData = benefitData;

        mBenefitDataMap = new HashMap<>();
        for(BenefitData bData : mBenefitData) {
            int year = bData.getAge().getYear();
            mBenefitDataMap.put(year, bData);
        }
    }

    @Override
    public BenefitData getBenefitData(AgeData age) {
        return mBenefitDataMap.get(age.getYear());
    }
}
