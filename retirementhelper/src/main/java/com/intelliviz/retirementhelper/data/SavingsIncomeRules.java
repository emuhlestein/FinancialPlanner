package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(String birthDate,  AgeData startAge, AgeData endAge,
                              double balance, double interest, double monthlyAddition, double withdrawPercent) {
        super(birthDate, startAge, endAge, balance, interest, monthlyAddition, withdrawPercent);
    }

    @Override
    protected double adjustMonthlyAmount(AgeData age, double amount) {
        return amount;
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        return false;
    }

    @Override
    public void setValues(Bundle bundle) {
    }

    @Override
    @Deprecated
    public MilestoneData getMilestone(AgeData age) {
        return null;
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
    }
}
