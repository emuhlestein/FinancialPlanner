package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(String birthDate,  AgeData endAge) {
        super(birthDate, endAge);
    }

    @Override
    protected double adjustMonthlyAmount(AgeData age, double amount) {
        return amount;
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        return false;
    }
}
