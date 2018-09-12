package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_EXHAUSTED;
import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.BALANCE_STATE_LOW;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(String ownerBirthDate, AgeData endAge, String otherBirthdate) {
        super(ownerBirthDate, endAge, otherBirthdate);
    }

    @Override
    protected IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance) {
        int balanceState = BALANCE_STATE_GOOD;
        if(balance == 0) {
            balanceState = BALANCE_STATE_EXHAUSTED;
        } else if(balance < monthlyAmount * 12) {
            balanceState = BALANCE_STATE_LOW;
        } else {
            balanceState = BALANCE_STATE_GOOD;
        }

        return new IncomeData(age, monthlyAmount, balance, balanceState);
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new SavingIncomeDataAccessor(getIncomeData());
    }
}
