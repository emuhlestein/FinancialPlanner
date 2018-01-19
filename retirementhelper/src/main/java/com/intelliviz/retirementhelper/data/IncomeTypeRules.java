package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import java.util.List;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    void setValues(Bundle bundle);
    List<AgeData> getAges();
    MilestoneData getMilestone(AgeData age);
    List<BenefitData> getBenefitData();
    BenefitData getBenefitForAge(AgeData age);
    double getBalanceForAge(AgeData age);
    BenefitData getBenefitData(BenefitData benefitData);
}
