package com.intelliviz.db.entity;

import com.intelliviz.data.SavingsData;

public class SavingsDataEntityMapper {
    public static SavingsData map(SavingsIncomeEntity sie) {
        SavingsData savingsData = new SavingsData(sie.getId(), sie.getType(), sie.getName(), sie.getOwner(), sie.getIncluded());
        savingsData.setAnnualPercentIncrease(sie.getAnnualPercentIncrease());
        savingsData.setBalance(sie.getBalance());
        savingsData.setInterest(sie.getInterest());
        savingsData.setMonthlyAddition(sie.getMonthlyAddition());
        savingsData.setShowMonths(sie.getShowMonths());
        savingsData.setStartAge(sie.getStartAge());
        savingsData.setStopMonthlyAdditionAge(sie.getStopMonthlyAdditionAge());
        savingsData.setWithdrawPercent(sie.getWithdrawPercent());
        return savingsData;
    }

    public static SavingsIncomeEntity map(SavingsData sd) {
        SavingsIncomeEntity sie = new SavingsIncomeEntity(sd.getId(), sd.getType(), sd.getName(),
                sd.getOwner(), sd.getIncluded(), sd.getStartAge(),
                sd.getBalance(), sd.getInterest(), sd.getMonthlyAddition(), sd.getStopMonthlyAdditionAge(),
                sd.getWithdrawPercent(), sd.getAnnualPercentIncrease(), sd.getShowMonths());
        return sie;
    }
}
