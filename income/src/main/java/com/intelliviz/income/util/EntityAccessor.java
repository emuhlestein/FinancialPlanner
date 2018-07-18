package com.intelliviz.income.util;


import com.intelliviz.income.viewmodel.ViewData;

/**
 * Created by edm on 2/24/2018.
 */

public interface EntityAccessor {
    ViewData getEntity(long id);
    int getMaxEntities();
    int getMaxErrorCode();
}
