package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    double getMonthlyBenefitForAge(AgeData age);
    AgeData getFullRetirementAge();
    AgeData getMinimumAge();
}
