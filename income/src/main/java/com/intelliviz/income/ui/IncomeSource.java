package com.intelliviz.income.ui;

import android.content.Context;

import com.intelliviz.db.entity.AbstractIncomeSource;

/**
 * Created by edm on 3/12/2018.
 */

public interface IncomeSource {
    void startAddActivity(Context context);
    void startEditActivity(Context context);
    void startDetailsActivity(Context context);
    AbstractIncomeSource getIncomeSourceEntity();
    long getId();
}
