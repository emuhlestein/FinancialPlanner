package com.intelliviz.retirementhelper.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    private AgeData mMinAge;
    private AgeData mEndAge;
    private double mMonthlyAmount;

    public PensionRules(AgeData minAge, AgeData endAge, double monthlyAmount) {
        mMinAge = minAge;
        mEndAge = endAge;
        mMonthlyAmount = monthlyAmount;
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    @Override
    public MilestoneData getMilestone(AgeData age) {
        MilestoneData milestone;
        if(age.isBefore(mMinAge)) {
            milestone = new MilestoneData(age, mEndAge, mMinAge, 0, 0, 0, 0, 0, 0, 0, 0);
        } else {
            int numMonths =  mEndAge.diff(age);

            milestone = new MilestoneData(age, mEndAge, mMinAge, mMonthlyAmount, 0, 0, 0, numMonths, 0, 0, 0);
        }
        return milestone;
    }

    public PensionData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mMinAge)) {
            return new PensionData(startAge, 0, 0);
        } else {
            return new PensionData(startAge, mMonthlyAmount, 2);
        }
    }
}
