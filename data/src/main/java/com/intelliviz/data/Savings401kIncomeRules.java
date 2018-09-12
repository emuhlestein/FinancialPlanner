package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_EXHAUSTED;
import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_LOW;

/**
 * Created by edm on 10/18/2017.
 */

public class Savings401kIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {
    private static final double PENALTY_PERCENT = 10.0;
    private static final AgeData PENALTY_AGE = new AgeData(59, 6);
    private AgeData mMinAge;
    private double mPenalty;

    public Savings401kIncomeRules(String ownerBirthdate, AgeData endAge, String otherBirthdate) {
        super(ownerBirthdate, endAge, otherBirthdate);
        mMinAge = PENALTY_AGE;
        mPenalty = PENALTY_PERCENT;
    }

    private double getPenaltyAmount(AgeData age, double amount) {
        if(isPenalty(age)) {
            return amount * PENALTY_PERCENT / 1200;
        } else {
            return 0;
        }
    }

    private boolean isPenalty(AgeData age) {
        if(age.isBefore(PENALTY_AGE)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance) {
        boolean isPenalty = isPenalty(age);

        if(isPenalty) {
            double penalyAmount = getPenaltyAmount(age, monthlyAmount);
            monthlyAmount -= penalyAmount;
        }

        int balanceState = BALANCE_STATE_GOOD;
        if(balance == 0) {
            balanceState = BALANCE_STATE_EXHAUSTED;
        } else if(balance < monthlyAmount * 12) {
            balanceState = BALANCE_STATE_LOW;
        }

        return new IncomeData(age, monthlyAmount, balance, balanceState, isPenalty);
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new Savings401kIncomeDataAccessor(getIncomeData());
    }
}
