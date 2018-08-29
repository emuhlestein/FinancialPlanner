package com.intelliviz.data;


import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 2/17/2018.
 */

public class IncomeSummaryHelper {
    public static List<IncomeData> getIncomeSummary(List<IncomeSourceEntityBase> incomeSourceList,
                                                              RetirementOptions roe) {
        List<IncomeData> benefitDataList = new ArrayList<>();
        List<IncomeDataAccessor> accessorList = new ArrayList<>();
        List<GovPension> gpList = new ArrayList<>();

        for(IncomeSourceEntityBase entity : incomeSourceList) {
            if(entity instanceof GovPensionEntity) {
                GovPensionEntity gpe = (GovPensionEntity)entity;
                GovPension gp = GovPensionEntityMapper.map(gpe);
                gpList.add(gp);
            } else if(entity instanceof PensionIncomeEntity) {
                PensionIncomeEntity pie = (PensionIncomeEntity)entity;
                PensionData pd = PensionDataEntityMapper.map(pie);
                PensionRules pr = new PensionRules(roe.getBirthdate(), roe.getEndAge(),
                        roe.getBirthdate());
                pd.setRules(pr);
                accessorList.add(pd.getIncomeDataAccessor());
            } else if(entity instanceof SavingsIncomeEntity) {
                SavingsIncomeEntity sie = (SavingsIncomeEntity)entity;
                SavingsData sd = SavingsDataEntityMapper.map(sie);
                if (sd.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                    SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), roe.getEndAge(), roe.getSpouseBirthdate());
                    sd.setRules(sir);
                    accessorList.add(sd.getIncomeDataAccessor());
                } else if (sd.getType() == RetirementConstants.INCOME_TYPE_401K) {
                    Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), roe.getEndAge(), roe.getSpouseBirthdate());
                    sd.setRules(tdir);
                    accessorList.add(sd.getIncomeDataAccessor());
                }
            }
        }

        if(!gpList.isEmpty()) {
            SocialSecurityRules.setRulesOnGovPensionEntities(gpList, roe);
            for (GovPension gp : gpList) {
                accessorList.add(gp.getIncomeDataAccessor());
            }
        }

        if(accessorList.isEmpty()) {
            return Collections.emptyList();
        }

        int minYear = 999;
        int maxYear = 0;
        List<Map<Integer, IncomeData>> mapListBenefitData = new ArrayList<>();
        for(int year = minYear; year <= maxYear; year++) {
            for (IncomeDataAccessor incomeDataAccessor : accessorList) {
            /*
            Map<Integer, IncomeData> benefitDataMap = new HashMap<>();
            mapListBenefitData.add(benefitDataMap);
            for(IncomeData benefitData : listBenefitData) {
                int year = benefitData.getAge().getYear();
                benefitDataMap.put(year, benefitData);
                if(year < minYear) {
                    minYear = year;
                }
                if(year > maxYear) {
                    maxYear = year;
                }
            }
            */
            }
        }

        AgeData age = AgeUtils.getAge(roe.getBirthdate());
        AgeData endAge = roe.getEndAge();

        for(int year = age.getYear(); year <= endAge.getYear(); year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;
            //AgeData age = incomeSourceEntityList.get(0).get(0).getAge();

            for(IncomeDataAccessor accessor : accessorList) {
                IncomeData benefitData = accessor.getIncomeData(new AgeData(year, 0));
                if(benefitData != null) {
                    sumBalance += benefitData.getBalance();
                    sumMonthlyWithdraw += benefitData.getMonthlyAmount();
                }
            }

            benefitDataList.add(new IncomeData(new AgeData(year, 0), sumMonthlyWithdraw, sumBalance, 0, false));
/*
            for (List<IncomeData> bdList : incomeSourceEntityList) {
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
