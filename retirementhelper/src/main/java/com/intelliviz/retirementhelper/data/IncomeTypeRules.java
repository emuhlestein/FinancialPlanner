package com.intelliviz.retirementhelper.data;

import java.util.List;

/**
 * Created by edm on 8/14/2017.
 */

public interface IncomeTypeRules {
    List<AgeData> getAges();
    MilestoneData getMilestone(AgeData age);
}
