package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    public static final AgeData DEFAULT_MIN_AGE = new AgeData(65, 0);
    private AgeData mMinAge;
    private AgeData mEndAge;
    private double mMonthlyAmount;

    public PensionRules(AgeData minAge, AgeData endAge, double monthlyAmount) {
        mMinAge = minAge;
        mEndAge = endAge;
        mMonthlyAmount = monthlyAmount;
    }

    @Override
    public void setValues(Bundle bundle) {
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

    @Override
    public List<BenefitData> getBenefitData() {
        AgeData age = mMinAge;
        List<BenefitData> listAmountDate = new ArrayList<>();

        BenefitData benefitData = new BenefitData(age, mMonthlyAmount, 0, 0, false);
        listAmountDate.add(benefitData);

        while(true) {
            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }

            age = new AgeData(nextAge.getYear(), 0);

            benefitData = new BenefitData(nextAge, mMonthlyAmount, 0, 0, false);
            listAmountDate.add(benefitData);
        }

        return listAmountDate;
    }

    @Override
    public double getBalanceForAge(AgeData age) {
        return 0;
    }

    @Override
    public BenefitData getBenefitForAge(AgeData age) {
        return null;
    }

    public PensionData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mMinAge)) {
            return new PensionData(startAge, 0, 0);
        } else {
            return new PensionData(startAge, mMonthlyAmount, 2);
        }
    }
}
