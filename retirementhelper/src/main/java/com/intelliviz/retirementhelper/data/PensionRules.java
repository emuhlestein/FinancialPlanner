package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.util.AgeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    public static final AgeData DEFAULT_MIN_AGE = new AgeData(65, 0);
    private AgeData mCurrentAge;
    private AgeData mMinAge;
    private AgeData mEndAge;
    private double mMonthlyAmount;

    public PensionRules(String birthDate, AgeData minAge, AgeData endAge, double monthlyAmount) {
        mCurrentAge = AgeUtils.getAge(birthDate);
        mMinAge = minAge;
        mEndAge = endAge;
        mMonthlyAmount = monthlyAmount;
    }

    @Override
    public void setValues(Bundle bundle) {
    }

    @Override
    public List<IncomeData> getIncomeData() {
        AgeData age = mCurrentAge;
        List<IncomeData> listAmountDate = new ArrayList<>();

        IncomeData benefitData;

        while(true) {

            age = new AgeData(age.getYear(), 0);

            if(age.isBefore(mMinAge)) {
                benefitData = new IncomeData(age, 0, 0, 0, false);
            } else {
                benefitData = new IncomeData(age, mMonthlyAmount, 0, 0, false);
            }
            listAmountDate.add(benefitData);

            // getList next age
            age = new AgeData(age.getYear()+1, 0);
            if(age.isAfter(mEndAge)) {
                break;
            }
        }

        return listAmountDate;
    }

    @Override
    public IncomeData getIncomeData(IncomeData incomeData) {
        return null;
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new PensionIncomeDataAccessor(mMinAge, mMonthlyAmount);
    }

    public PensionData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mMinAge)) {
            return new PensionData(startAge, 0, 0);
        } else {
            return new PensionData(startAge, mMonthlyAmount, 2);
        }
    }
}
