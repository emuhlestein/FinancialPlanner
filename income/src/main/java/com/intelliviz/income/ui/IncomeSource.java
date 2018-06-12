package com.intelliviz.income.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.income.db.entity.IncomeSourceEntityBase;

/**
 * Created by edm on 3/12/2018.
 */

public interface IncomeSource {
    void startAddActivity(FragmentActivity activity);
    void startEditActivity(FragmentActivity activity);
    void startDetailsActivity(Context context);
    IncomeSourceEntityBase getIncomeSourceEntity();
    long getId();
}
