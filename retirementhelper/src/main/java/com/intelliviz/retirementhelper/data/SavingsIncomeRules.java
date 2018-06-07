package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(String birthDate,  AgeData endAge) {
        super(birthDate, endAge);
    }

    @Override
    protected double getPenaltyAmount(AgeData age, double amount) {
        return 0;
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        return false;
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new SavingIncomeDataAccessor(getBenefitData());
    }
}
