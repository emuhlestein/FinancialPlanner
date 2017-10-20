package com.intelliviz.retirementhelper.data;

import java.util.List;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    double getMonthlyBenefitForAge(AgeData age);
    List<AgeData> getAges();
    MilestoneData getMilestone(AgeData age);
}
