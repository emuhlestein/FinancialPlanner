package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_SEVERE;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_WARNING;

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
        if(isPenalty(age, amount)) {
            return amount * PENALTY_PERCENT / 100;
        } else {
            return 0;
        }
    }

    private boolean isPenalty(AgeData age, double amount) {
        if(age.isBefore(PENALTY_AGE) && amount > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance) {
        String message = null;
        int status = SC_GOOD;
        if(isPenalty(age, monthlyAmount)) {
            monthlyAmount -= getPenaltyAmount(age, monthlyAmount);
            status = SC_WARNING;
            message = "There is a 10% penalty for early withdrawal.";
        } else if(balance == 0) {
            status = SC_SEVERE;
            message = "Balance has been exhausted. Need to increase savings, reduce initial monthly withdraw or delay retirement.";
        } else if(balance < monthlyAmount * 12) {
            status = SC_WARNING;
            message = "Balance will be exhausted in less than a year";
        }

        return new IncomeData(age, monthlyAmount, balance, status, message);
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new Savings401kIncomeDataAccessor(getIncomeData(), getOwner());
    }
}
