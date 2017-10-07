package com.intelliviz.retirementhelper.data;

import android.os.Parcelable;

import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;

import java.util.List;

/**
 * Interface for income types.
 * Created by Ed Muhlestein on 5/22/2017.
 */
public interface IncomeType extends Parcelable {

    /**
     * Get the balance.
     * @return The balance.
     */
    double getBalance();

    /**
     * Get the monthly benefit.
     *
     * @param age The age.
     * @return The monthly benefir.
     */
    double getMonthlyBenefitForAge(AgeData age);

    double getFullMonthlyBenefit();

    List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsData rod);

    List<AgeData> getAges();
}
