package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    double getMonthlyBenefitForAge(AgeData age);
    double getFullMonthlyBenefit();
    AgeData getFullRetirementAge();
    AgeData getMinimumAge();
}
