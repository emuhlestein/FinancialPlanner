package com.intelliviz.retirementhelper.viewmodel;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 2/17/2018.
 */

public class IncomeSummaryHelper {
    public static List<BenefitData> getIncomeSummary(AppDatabase mDB, RetirementOptionsEntity roe) {
        List<BenefitData> benefitDataList = new ArrayList<>();

        List<SavingsIncomeEntity> sieList = mDB.savingsIncomeDao().get();
        List<List<BenefitData>> incomeSourceEntityList = new ArrayList<>();
        for (SavingsIncomeEntity sie : sieList) {
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(sir);
                incomeSourceEntityList.add(sie.getBenefitData());
            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(tdir);
                incomeSourceEntityList.add(sie.getBenefitData());
            }
        }

        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for (GovPensionEntity gpe : gpeList) {
            incomeSourceEntityList.add(gpe.getBenefitData());
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for (PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, roe.getEndAge(), Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            incomeSourceEntityList.add(pie.getBenefitData());
        }

        if(incomeSourceEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        int minYear = 999;
        int maxYear = 0;
        List<Map<Integer, BenefitData>> mapListBenefitData = new ArrayList<>();
        for(List<BenefitData> listBenefitData : incomeSourceEntityList) {
            Map<Integer, BenefitData> benefitDataMap = new HashMap<>();
            mapListBenefitData.add(benefitDataMap);
            for(BenefitData benefitData : listBenefitData) {
                int year = benefitData.getAge().getYear();
                benefitDataMap.put(year, benefitData);
                if(year < minYear) {
                    minYear = year;
                }
                if(year > maxYear) {
                    maxYear = year;
                }
            }
        }

        for(int year = minYear; year <= maxYear; year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;
            //AgeData age = incomeSourceEntityList.get(0).get(0).getAge();

            for(Map<Integer, BenefitData> mapBenefitData : mapListBenefitData) {
                BenefitData benefitData = mapBenefitData.get(year);
                if(benefitData != null) {
                    sumBalance += benefitData.getBalance();
                    sumMonthlyWithdraw += benefitData.getMonthlyAmount();
                }
            }

            benefitDataList.add(new BenefitData(new AgeData(year, 0), sumMonthlyWithdraw, sumBalance, 0, false));
/*
            for (List<BenefitData> bdList : incomeSourceEntityList) {
                benefitData = bdList.get(year);
                age = new AgeData(benefitData.getAge().getNumberOfMonths());
                sumBalance += benefitData.getBalance();
                sumMonthlyWithdraw += benefitData.getMonthlyAmount();
            }
*/
        }

        return benefitDataList;
    }
}
