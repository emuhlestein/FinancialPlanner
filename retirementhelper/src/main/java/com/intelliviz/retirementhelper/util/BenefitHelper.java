package com.intelliviz.retirementhelper.util;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {
    public static String getMonthlyBenefit(Context context, IncomeType incomeType) {
        if(incomeType instanceof SavingsIncomeData) {
            return getMonthlyBenefitFromSavings(context, (SavingsIncomeData)incomeType);
        } else {
            return "0.00";
        }
    }

    /**
     * Get monthly benefit for specified age.
     * @param context
     * @param sid
     * @return
     */
    // TODO should pass in age not sid
    private static String getMonthlyBenefitFromSavings(Context context, SavingsIncomeData sid) {
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(context);
        String birthdate = pid.getBirthdate();
        AgeData age = SystemUtils.getAge(birthdate);

        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(context);
        String strEndAge = rod.getEndAge();
        int endAge = Integer.parseInt(strEndAge);
        int numYears = endAge - age.getYear();
        List<BalanceData> bdList = sid.getBalanceDataList();
        BalanceData bd = bdList.get(0);
        String balance = bd.getBalance();

        BigDecimal bdBalance = new BigDecimal(balance);
        String years = String.valueOf(numYears);
        BigDecimal bdYears = new BigDecimal(years);
        BigDecimal bdMonths = new BigDecimal("12");
        BigDecimal totalMonths = bdYears.multiply(bdMonths);
        BigDecimal monthlyBenefit = bdBalance.divide(totalMonths, 2, RoundingMode.HALF_UP);
        int mode = rod.getWithdrawMode();
        switch(mode) {
            case RetirementConstants.WITHDRAW_MODE_NO_REDUC:
                // live off interest
                BigDecimal bdInterest = new BigDecimal(sid.getInterest());
                BigDecimal bdConvert = new BigDecimal("1200");
                BigDecimal bdMonthlyInterest = bdInterest.divide(bdConvert);
                monthlyBenefit = bdBalance.multiply(bdMonthlyInterest);
                return monthlyBenefit.toString();
            case RetirementConstants.WITHDRAW_MODE_PERCENT:
                break;
            case RetirementConstants.WITHDRAW_MODE_ZERO_PRI:
                break;
        }
        return monthlyBenefit.toString();
    }
}
