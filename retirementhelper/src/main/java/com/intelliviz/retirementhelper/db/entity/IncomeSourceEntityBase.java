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
}
