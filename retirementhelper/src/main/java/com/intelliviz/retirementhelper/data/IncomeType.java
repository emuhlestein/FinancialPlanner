package com.intelliviz.retirementhelper.data;

import android.os.Parcelable;

/**
 * Created by edm on 5/22/2017.
 */

public interface IncomeType extends Parcelable {
    long getId();
    String getName();
    int getType();
    boolean hasABalance();
    double getBalance();
    double getMonthlyBenefit(double withdrawalRate);
}
