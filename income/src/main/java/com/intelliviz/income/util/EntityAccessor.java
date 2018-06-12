package com.intelliviz.income.util;


import com.intelliviz.income.viewmodel.LiveDataWrapper;

/**
 * Created by edm on 2/24/2018.
 */

public interface EntityAccessor {
    LiveDataWrapper getEntity(long id);
    int getMaxEntities();
    int getMaxErrorCode();
}
