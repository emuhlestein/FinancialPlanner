package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Utility class for database access.
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {

    public static List<MilestoneData> getAllMilestones(AppDatabase mDB) {
        List<IncomeSourceEntityBase> allEntities = getAllIncomeEntities(mDB);
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
        List<MilestoneAgeEntity> ages = getMilestoneAges(mDB);
        return getAllMilestones(allEntities, ages, rod);
    }

    public static List<IncomeSourceEntityBase> getAllIncomeEntities(AppDatabase mDB) {
        List<IncomeSourceEntityBase> allEntities = new ArrayList<>();
        List<GovPensionEntity> govEntities = mDB.govPensionDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        String birthdate = roe.getBirthdate();
        AgeData endAge = SystemUtils.parseAgeString(roe.getEndAge());
        if(!govEntities.isEmpty()) {
            for(GovPensionEntity gpe : govEntities) {
                SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge,
                        Double.parseDouble(gpe.getFullMonthlyBenefit()), gpe.getSpouse(),
                                Double.parseDouble(gpe.getSpouseBenefit()), gpe.getSpouseBirhtdate());
                gpe.setRules(ssr);
                allEntities.add(gpe);
            }
        }
        List<PensionIncomeEntity> pensionEntities = mDB.pensionIncomeDao().get();
        if(!pensionEntities.isEmpty()) {
            for(PensionIncomeEntity pie : pensionEntities) {
                AgeData minAge = SystemUtils.parseAgeString(pie.getMinAge());
                PensionRules pr = new PensionRules(minAge, endAge, Double.parseDouble(pie.getMonthlyBenefit()));
                pie.setRules(pr);
                allEntities.add(pie);
            }
        }
        List<SavingsIncomeEntity> savingsEntities = mDB.savingsIncomeDao().get();
        if(!savingsEntities.isEmpty()) {
            for(SavingsIncomeEntity se : savingsEntities) {
                SavingsIncomeRules sir = new SavingsIncomeRules(birthdate, endAge,  Double.parseDouble(se.getBalance()),
                        Double.parseDouble(se.getInterest()), Double.parseDouble(se.getMonthlyIncrease()),
                                roe.getWithdrawMode(), Double.parseDouble(roe.getWithdrawAmount()));
                se.setRules(sir);
                allEntities.add(se);
            }
        }
        List<TaxDeferredIncomeEntity> taxDefEntities = mDB.taxDeferredIncomeDao().get();
        if(!taxDefEntities.isEmpty()) {
            double amount = Double.parseDouble(roe.getWithdrawAmount());
            for(TaxDeferredIncomeEntity tde : taxDefEntities) {
                TaxDeferredIncomeRules tdir = new TaxDeferredIncomeRules(birthdate, endAge, Double.parseDouble(tde.getBalance()),
                        Double.parseDouble(tde.getInterest()), Double.parseDouble(tde.getMonthlyIncrease()), roe.getWithdrawMode(),
                        amount);
                tde.setRules(tdir);
                allEntities.add(tde);
            }
        }
        return allEntities;
    }

    public static List<MilestoneAgeEntity> getMilestoneAges(AppDatabase mDB) {
        List<IncomeSourceEntityBase> incomeTypes = getAllIncomeEntities(mDB);
        List<MilestoneAgeEntity> ageEntities = mDB.milestoneAgeDao().getAges();
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();

        HashSet<MilestoneAgeEntity> ages = new HashSet<>();
        for(MilestoneAgeEntity age : ageEntities) {
            long id = age.getId();
            ages.add(new MilestoneAgeEntity(id, age.getAge()));
        }

        // add ages from income types
        for(IncomeSourceEntityBase incomeType : incomeTypes) {
            List<AgeData> allAges = incomeType.getAges();
            for(AgeData anAge : allAges) {
                ages.add(new MilestoneAgeEntity(0, anAge));
            }
        }

        String birthdate = rod.getBirthdate();
        if(SystemUtils.validateBirthday(birthdate)) {
            // add birth date
            AgeData nowAge = SystemUtils.getAge(birthdate);
            ages.add(new MilestoneAgeEntity(0, nowAge));
        }

        // Need to get this age from deferred tax income source
        //AgeData min401kAge = new AgeData(59, 6);
        //ages.add(new MilestoneAgeData(-1, min401kAge));

        List<MilestoneAgeEntity> sortedAges = new ArrayList<>(ages);
        Collections.sort(sortedAges);

        return sortedAges;
    }

    public static List<MilestoneData> getAllMilestones(List<IncomeSourceEntityBase> incomeTypes, List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        List<MilestoneData> sumMilestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return sumMilestones;
        }

        if(incomeTypes == null || incomeTypes.isEmpty()) {

            for(MilestoneAgeEntity msad : ages) {
                MilestoneData msd = new MilestoneData(msad.getAge());
                sumMilestones.add(msd);
            }
            return sumMilestones;
        }

        double[] sumMonthlyAmount = new double[ages.size()];
        double[] sumStartBalance = new double[ages.size()];
        double[] sumEndBalance = new double[ages.size()];
        for(int i = 0; i < ages.size(); i++) {
            sumMonthlyAmount[i] = 0;
            sumStartBalance[i] = 0;
            sumEndBalance[i] = 0;
        }

        int numMonthsFundsWillLast = 0;
        List<MilestoneData> saveMilestones = null;
        for(IncomeSourceEntityBase incomeType : incomeTypes) {
            List<MilestoneData> milestones = incomeType.getMilestones(ages, rod);
            if(milestones == null || milestones.isEmpty()) {
                continue;
            }

            if(saveMilestones == null) {
                saveMilestones = milestones;
            }

            double monthlyAmount;
            double startBalance;
            double endBalance;
            for(int i = 0; i < milestones.size(); i++) {
                MilestoneData milestoneData = milestones.get(i);
                monthlyAmount = milestoneData.getMonthlyBenefit();
                sumMonthlyAmount[i] += monthlyAmount;
                startBalance = milestoneData.getStartBalance();
                sumStartBalance[i] += startBalance;
                endBalance = milestoneData.getEndBalance();
                sumEndBalance[i] += endBalance;

                int numMonths = milestoneData.getMonthsFundsFillLast();
                if(numMonths > numMonthsFundsWillLast) {
                    numMonthsFundsWillLast = numMonths;
                }
            }
        }

        AgeData endAge = saveMilestones.get(0).getEndAge();
        AgeData minimumAge = saveMilestones.get(0).getMinimumAge();
        for(int i = 0; i < ages.size(); i++) {
            AgeData startAge = ages.get(i).getAge();
            MilestoneData milestoneData = new MilestoneData(startAge, endAge, minimumAge, sumMonthlyAmount[i], sumStartBalance[i], sumEndBalance[i], 0, numMonthsFundsWillLast);
            sumMilestones.add(milestoneData);
        }

        return sumMilestones;
    }

    static void updateSummaryData(AppDatabase db) {
        db.summaryDao().deleteAll();
        List<MilestoneData> milestones = getAllMilestones(db);
        for(MilestoneData msd : milestones) {
            db.summaryDao().insert(new SummaryEntity(0, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
    }
}
