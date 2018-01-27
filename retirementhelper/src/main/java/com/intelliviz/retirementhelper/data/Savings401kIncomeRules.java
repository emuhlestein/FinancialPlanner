package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 10/18/2017.
 */

public class Savings401kIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {
    private static final double PENALTY_PERCENT = 10.0;
    private static final AgeData PENALTY_AGE = new AgeData(59, 6);
    private AgeData mMinAge;
    private double mPenalty;

    public Savings401kIncomeRules(String birthDate,  AgeData endAge) {
        super(birthDate, endAge);
        mMinAge = PENALTY_AGE;
        mPenalty = PENALTY_PERCENT;
    }

    @Override
    protected double adjustMonthlyAmount(AgeData age, double amount) {
        double penaltyPercent = (100 - PENALTY_PERCENT)/1200;
        if(isPenalty(age)) {
            return amount * penaltyPercent;
        } else {
            return amount;
        }
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        if(age.isBefore(PENALTY_AGE)) {
            return true;
        } else {
            return false;
        }
    }
}
