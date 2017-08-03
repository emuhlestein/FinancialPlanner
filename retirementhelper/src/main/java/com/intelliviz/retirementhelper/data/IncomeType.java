package com.intelliviz.retirementhelper.data;

import android.content.Context;
import android.os.Parcelable;

import java.util.List;

/**
 * Interface for income types.
 * Created by Ed Muhlestein on 5/22/2017.
 */
public interface IncomeType extends Parcelable {
    /**
     * Get the database id.
     * @return The database id.
     */
    long getId();

    /**
     * Get the name.
     * @return The name.
     */
    String getName();

    /**
     * Get the type.
     * @return The type.
     */
    int getType();

    /**
     * Get the balance.
     * @return The balance.
     */
    double getBalance();

    /**
     * Get the monthly benefit base on rate of withdraw.
     *
     * @param withdrawalRate The monthly benefit.
     * @return The monthly benefir.
     */
    double getMonthlyBenefit(double withdrawalRate);

    List<MilestoneData> getMilestones(Context context, RetirementOptionsData rod);
}
