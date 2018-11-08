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

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 2/17/2018.
 */

public class IncomeSummaryHelper {
    public static List<IncomeData> getIncomeSummary(List<IncomeSourceEntityBase> incomeSourceList,
                                                              RetirementOptions ro) {
        List<IncomeData> benefitDataList = new ArrayList<>();
        List<IncomeDataAccessor> accessorList = new ArrayList<>();
        List<GovPension> gpList = new ArrayList<>();
        List<IncomeSourceData> incomeSourceDataList = new ArrayList<>();

        for(IncomeSourceEntityBase entity : incomeSourceList) {
            if(entity instanceof GovPensionEntity) {
                GovPensionEntity gpe = (GovPensionEntity)entity;
                if(gpe.getIncluded() == 1) {
                    GovPension gp = GovPensionEntityMapper.map(gpe);
                    gpList.add(gp);
                }
            } else if(entity instanceof PensionIncomeEntity) {
                PensionIncomeEntity pie = (PensionIncomeEntity)entity;
                PensionData pd = PensionDataEntityMapper.map(pie);
                if(pd.getIncluded() == 1) {
                    pd.setRules(new PensionRules(ro));
                    incomeSourceDataList.add(pd);
                }
            } else if(entity instanceof SavingsIncomeEntity) {
                SavingsIncomeEntity sie = (SavingsIncomeEntity)entity;
                SavingsData sd = SavingsDataEntityMapper.map(sie);
                if (sd.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                    SavingsIncomeRules sir = new SavingsIncomeRules(ro, true);
                    if(sd.getIncluded() == 1) {
                        sd.setRules(sir);
                        incomeSourceDataList.add(sd);
                    }
                } else if (sd.getType() == RetirementConstants.INCOME_TYPE_401K) {
                    Savings401kIncomeRules tdir = new Savings401kIncomeRules(ro, true);
                    if(sd.getIncluded() == 1) {
                        sd.setRules(tdir);
                        incomeSourceDataList.add(sd);
                    }
                }
            }
        }

        if(!gpList.isEmpty()) {
            SocialSecurityRules.setRulesOnGovPensionEntities(gpList, ro, true);
            for (GovPension gp : gpList) {
                incomeSourceDataList.add(gp);
            }
        }

        if(incomeSourceDataList.isEmpty()) {
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

        AgeData age = AgeUtils.getAge(ro.getPrimaryBirthdate());
        AgeData endAge = ro.getEndAge();

        for(int year = age.getYear(); year <= endAge.getYear(); year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;

            for(IncomeSourceData incomeSource : incomeSourceDataList) {
                IncomeData benefitData = incomeSource.getIncomeData(new AgeData(year, 0));
                if(benefitData != null) {
                    sumBalance += benefitData.getBalance();
                    sumMonthlyWithdraw += benefitData.getMonthlyAmount();
                }
            }

            benefitDataList.add(new IncomeData(new AgeData(year, 0), sumMonthlyWithdraw, sumBalance, SC_GOOD, null));
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

    /**
     * Get the age in terms of the owner of the income source. If owner is self,
     * no need to convert. If owner is spouse, need to convert.
     * @param age Age of principle (primary) spouse where owner is self.
     * @param owner Self or spouse.
     * @param ro The retirement options.
     * @return The age of the owner.
     */
    public static AgeData getOwnerAge(AgeData age, int owner, RetirementOptions ro) {
        if(owner == OWNER_SPOUSE) {
            return AgeUtils.getAge(ro.getSpouseBirthdate(), ro.getPrimaryBirthdate(), age);
        } else {
            return age;
        }
    }

}
