package com.intelliviz.retirementhelper.data;

import android.os.Parcel;

import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Class for a basic income type.
 * Created by Ed Muhlestein on 5/12/2017.
 */

public abstract class IncomeTypeData implements IncomeType {
    /**
     * The database id.
     */
    private long id;

    /**
     * The income type. Can be one of following:
     *      INCOME_TYPE_SAVINGS
     *      INCOME_TYPE_TAX_DEFERRED
     *      INCOME_TYPE_PENSION
     *      INCOME_TYPE_GOV_PENSION
     */
    private int type;

    /**
     * The name of the income type.
     */
    private String name;

    /**
     * Default constructor.
     */
    IncomeTypeData() {
        this(0, 0, "");
    }

    /**
     * Constructor.
     * @param type The income type.
     */
    IncomeTypeData(int type) {
        this(0, type, "");
    }

    IncomeTypeData(long id, int type, String name) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
            AgeData diffAge = age.subtract(refAge);
            int numMonths = diffAge.getNumberOfMonths();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(type);
    }

    public void readFromParcel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        type = in.readInt();
    }
}
