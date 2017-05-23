package com.intelliviz.retirementhelper.util;

import android.os.Parcelable;

/**
 * Created by edm on 5/22/2017.
 */

public interface IncomeType extends Parcelable {
    long getId();
    String getName();
    int getType();
}
