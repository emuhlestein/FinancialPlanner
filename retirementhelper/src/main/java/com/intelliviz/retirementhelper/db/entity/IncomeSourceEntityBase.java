package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.PrimaryKey;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.IncomeSourceType;
import com.intelliviz.retirementhelper.data.MilestoneData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/3/2017.
 */

public abstract class IncomeSourceEntityBase implements IncomeSourceType {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int type;

    private String name;

    public IncomeSourceEntityBase(long id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public abstract List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod);

    public abstract List<AgeData> getAges();

    public abstract List<BenefitData> getBenefitData();


    /**
     * Get the balances for each milestone.
     * @param balance The beginning balance.
     * @param interest The annual interest rate.
     * @param monthlyIncrease The monthly increase.
     * @return The list of balances.
     */
    protected static List<Double> getMilestoneBalances(List<MilestoneAgeEntity> ages, double balance, double interest, double monthlyIncrease) {
        List<Double> balances = new ArrayList<>();
        if(ages.isEmpty()) {
            return balances;
        }
        AgeData refAge = ages.get(0).getAge();
        double newBalance = balance;
        balances.add(newBalance);
        for (int i = 1; i < ages.size(); i++) {
            AgeData age = ages.get(i).getAge();
            int numMonths =  age.diff(refAge);
            newBalance = getFutureBalance(newBalance, numMonths, interest, monthlyIncrease);
            balances.add(newBalance);
            refAge = age;
        }

        return balances;
    }

    private static double getFutureBalance(double balance, int numMonths, double interest, double monthlyIncrease) {
        double cumulativeBalance = balance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getBalance(cumulativeBalance, interest, monthlyIncrease);
        }
        return cumulativeBalance;
    }

    private static double getBalance(double balance, double interest, double monthlyIncrease) {
        double interestEarned = getMonthlyAmountFromBalance(balance, interest);
        return monthlyIncrease + interestEarned + balance;
    }

    private static double getMonthlyAmountFromBalance(double balance, double interest) {
        double monthlyInterest = interest / 1200.0;
        return balance * monthlyInterest;
    }

    /*
    protected static List<MilestoneData> getMilestones(AgeData endOfLifeAge, AgeData minimumAge,
                                                       double interestRate, double penalty, int withdrawMode, double withdrawAmount,
                                                       List<MilestoneAgeEntity> ages, List<Double> milestoneBalances) {

        List<MilestoneData> milestones = new ArrayList<>();

        for(int i = 0; i < ages.size(); i++) {
            AgeData startAge = ages.get(i).getAge();
            if(minimumAge == null) {
                penalty = 0;
            } else {
                if (!startAge.isBefore(minimumAge)) {
                    penalty = 0;
                }
            }
            double startBalance = milestoneBalances.get(i);
            double monthlyAmount = getMonthlyAmount(startBalance, withdrawMode, withdrawAmount);
            MilestoneData milestoneData = getMonthlyBalances(startAge, endOfLifeAge, minimumAge,
                    startBalance, interestRate, monthlyAmount, penalty);
            milestones.add(milestoneData);
        }
        return milestones;
    }
    */
/*
    private static double getMonthlyAmount(double balance, int withdrawMode, double withdrawAmount) {
        double monthlyAmount;
        switch(withdrawMode) {
            case WITHDRAW_MODE_AMOUNT:
                monthlyAmount = withdrawAmount;
                break;
            case WITHDRAW_MODE_PERCENT:
                monthlyAmount = getMonthlyAmountFromBalance(balance, withdrawAmount);
                break;
            default:
                monthlyAmount = withdrawAmount;
        }
        return monthlyAmount;
    }
    */
/*
    private static MilestoneData getMonthlyBalances(AgeData startAge, AgeData endAge, AgeData minimumAge,
                                                    double startBalance, double interestRate, double monthlyAmount,
                                                    double penalty) {
        AgeData age = endAge.subtract(startAge);
        int numMonthsInRetirement = age.getNumberOfMonths();
        double lastBalance = startBalance;
        double monthlyInterest = interestRate / 1200;

        int numMonths = 0;
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;
            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        return new MilestoneData(startAge, endAge, minimumAge, monthlyAmount, startBalance, lastBalance, penalty, numMonths);
    }
    */

}
