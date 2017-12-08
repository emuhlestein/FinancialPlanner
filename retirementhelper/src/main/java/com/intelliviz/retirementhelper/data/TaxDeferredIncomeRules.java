package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/18/2017.
 */

public class TaxDeferredIncomeRules implements IncomeTypeRules {
    private static final double PENALTY_PERCENT = 10.0;
    private static final AgeData PENALTY_AGE = new AgeData(59, 6);
    private AgeData mMinAge;
    private AgeData mCurrentAge;
    private AgeData mEndAge;
    private AgeData mStartAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private double mPenalty;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public TaxDeferredIncomeRules(String birthDate, AgeData endAge, AgeData startAge, double balance,
                                  double interest, double monthlyIncrease, int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mMinAge = PENALTY_AGE;
        mStartAge = startAge;
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mPenalty = PENALTY_PERCENT;
        mBalance = balance;
        mEndAge = endAge;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    public MilestoneData getMilestone(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return null;
        }
        int numMonths =  age.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = BalanceUtils.getMonthlyAmount(futureBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData =
                BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount, mWithdrawMode, mWithdrawAmount);

        if(age.isBefore(mMinAge)) {

        }

        return milestoneData;
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    private AmountData getMonthlyBenefit(AgeData age, double balance, AmountData amountData) {
        if(age.isBefore(mCurrentAge)) {
            return null;
        }

        int numMonths = age.diff(mCurrentAge);
        if(amountData == null) {
            double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);
            double monthlyAmount = BalanceUtils.getMonthlyAmount(futureBalance, mWithdrawMode, mWithdrawAmount);
            return new AmountData(age, monthlyAmount, futureBalance, 0, false);
        } else {
            if(age.isBefore(amountData.getAge())) {
                return null;
            }
            return null;
        }
    }

    public TaxDeferredData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mCurrentAge)) {
            return null;
        }

        int numMonths =  startAge.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        return getMilestoneData(startAge, mEndAge, mInterest, futureBalance, mWithdrawMode, mWithdrawAmount);
    }

    private TaxDeferredData getMilestoneData(AgeData startAge, AgeData endAge, double interest,
                                                 double startBalance, int withdrawMode, double withdrawAmount) {
        if(!startAge.isBefore(endAge)) {
            return null;
        }
        int numMonthsInRetirement = endAge.diff(startAge);
        double lastBalance = startBalance;
        double monthlyInterest = interest / 1200;

        double initialAmount;
        if(withdrawMode == WITHDRAW_MODE_PERCENT) {
            double percent = withdrawAmount / 1200;
            initialAmount = startBalance * percent;
        } else {
            initialAmount = withdrawAmount;
        }

        double monthlyAmount = initialAmount;
        double monthlyAmountIncrease = 0;

        int numMonths = 0;
        AgeData countAge = new AgeData();
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;

            countAge.add(1);
            if(countAge.getMonth() == 0) {
                // new year, increase withdraw amount
                double amountToIncrease = monthlyAmount * monthlyAmountIncrease;
                monthlyAmount += amountToIncrease;
            }

            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        double annutalAmount = monthlyAmount * 12;
        int status;
        if(lastBalance == 0) {
            status = 0;
        } else if(lastBalance < annutalAmount) {
            status = 1;
        } else {
            status = 2;
        }

        // Need the following:
        // Start Balance, End Balance, num months, init withdraw amount, final withdraw amount, did money run out
        return new TaxDeferredData(startAge, numMonths, startBalance, lastBalance, initialAmount, monthlyAmount, status);
    }

    public List<AmountData> getMonthlyAmountData() {
        AgeData age = mStartAge;

        double penaltyPercent = (100 - PENALTY_PERCENT)/1200;
        int numMonths = mStartAge.diff(mCurrentAge);
        double balance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);
        double monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);

        List<AmountData> listAmountDate = new ArrayList<>();

        boolean penalty = false;
        if(age.isBefore(PENALTY_AGE)) {
            penalty = true;
        }
        int balanceState = 2;
        AmountData amountData = new AmountData(age, monthlyWithdrawAmount, balance, balanceState, penalty);
        listAmountDate.add(amountData);

        while(true) {
            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }
            numMonths = nextAge.diff(age);

            age = new AgeData(nextAge.getYear(), 0);

            balance = getNewBalance(numMonths, balance, monthlyWithdrawAmount, mInterest);
            if(balance < 0) {
                balance = 0;
                balanceState = 0;
            } else {
                if(monthlyWithdrawAmount*12 > balance) {
                    balanceState = 1;
                }
            }

            // increase month withdraw amount
            double mWithdrawPercentIncrease = 0;
            double withdrawAmountIncrease = monthlyWithdrawAmount * mWithdrawPercentIncrease / 1200;
            monthlyWithdrawAmount += withdrawAmountIncrease;

            penalty = false;
            if(nextAge.isBefore(PENALTY_AGE)) {
                penalty = true;
                monthlyWithdrawAmount *= penaltyPercent;
            }
            amountData = new AmountData(nextAge, monthlyWithdrawAmount, balance, balanceState, penalty);
            listAmountDate.add(amountData);
        }

        return listAmountDate;
    }

    private double getInitMonthlyWithdrawAmount(double balance) {
        if(mWithdrawMode == WITHDRAW_MODE_PERCENT) {
            return balance * mWithdrawAmount / 1200;
        } else {
            return mWithdrawAmount;
        }
    }

    private double getNewBalance(int numMonths, double balance, double monthlyWithdrawAmount, double annualInterest) {
        double monthlyInterest = annualInterest / 1200;
        double newBalance = balance;
        for(int month = 0; month < numMonths; month++) {
            newBalance -= monthlyWithdrawAmount;
            double monthlyIncrease = newBalance * monthlyInterest;
            newBalance += monthlyIncrease;
        }

        return newBalance;
    }
}
