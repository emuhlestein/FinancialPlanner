package com.intelliviz.retirementhelper.util;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.util.SystemUtils.getAge;

/**
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {
    public static String getMonthlyBenefit(Context context, IncomeType incomeType) {
        if(incomeType instanceof SavingsIncomeData) {
            return getMonthlyBenefitFromSavings(context, (SavingsIncomeData)incomeType);
        } else if(incomeType instanceof TaxDeferredIncomeData) {
            return getMonthlyBenefitFromTaxDeferredIncome(context, (TaxDeferredIncomeData)incomeType);
        } else {
            return "0.00";
        }
    }

    private static String getMonthlyBenefitFromTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid) {
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(context);
        String birthdate = pid.getBirthdate();
        AgeData age = getAge(birthdate);

        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(context);
        String strEndAge = rod.getEndAge();
        int endAge = Integer.parseInt(strEndAge);
        int numYears = endAge - age.getYear();
        List<BalanceData> bdList = tdid.getBalanceDataList();
        BalanceData bd = bdList.get(0);
        String balance = bd.getBalance();
        BigDecimal bdBalance = new BigDecimal(balance);
        String years = String.valueOf(numYears);
        BigDecimal bdYears = new BigDecimal(years);
        BigDecimal bdMonths = new BigDecimal("12");
        BigDecimal totalMonths = bdYears.multiply(bdMonths);
        BigDecimal monthlyBenefit = bdBalance.divide(totalMonths, 2, RoundingMode.HALF_UP);

        BigDecimal penaltyAmount = new BigDecimal("1");
        if(tdid.getIs401k() == 1) {
            float fage = age.getYear();
            fage += age.getMonth()/12.0f;

            float page = Float.parseFloat(tdid.getMinimumAge());
            if(fage < page) {
                BigDecimal penaltyInterest = new BigDecimal(tdid.getPenalty());
                BigDecimal bdConvert = new BigDecimal("1200");
                penaltyAmount = penaltyInterest.divide(bdConvert, 2, RoundingMode.HALF_UP);
            }

        }
        int mode = rod.getWithdrawMode();
        switch(mode) {
            case RetirementConstants.WITHDRAW_MODE_NO_REDUC:
                // live off interest
                BigDecimal bdInterest = new BigDecimal(tdid.getInterest());
                BigDecimal bdConvert = new BigDecimal("1200");
                BigDecimal bdMonthlyInterest = bdInterest.divide(bdConvert, 2, RoundingMode.HALF_UP);
                monthlyBenefit = bdBalance.multiply(bdMonthlyInterest);
                monthlyBenefit = monthlyBenefit.multiply(penaltyAmount);
                return monthlyBenefit.toString();
            case RetirementConstants.WITHDRAW_MODE_PERCENT:
                break;
            case RetirementConstants.WITHDRAW_MODE_ZERO_PRI:
                break;
        }
        return monthlyBenefit.toString();
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
        AgeData age = getAge(birthdate);

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

    public static List<String> getMilestones(Context context) {
        List<String> milestones = DataBaseUtils.getMilestoneData(context);
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(context);
        AgeData ageData = SystemUtils.getAge(pid.getBirthdate());

        int year = ageData.getYear();
        int month = ageData.getMonth();

        float fyear = year + month / 12.0f;

        String ageNow = Float.toString(fyear);
        milestones.add(ageNow);
        milestones.add("67.666");
        Collections.sort(milestones);

        return milestones;
    }
}
