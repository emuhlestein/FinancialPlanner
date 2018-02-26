package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.viewmodel.LiveDataWrapper;

/**
 * Created by edm on 2/24/2018.
 */

public interface EntityAccessor {
    LiveDataWrapper getEntity(long id);
    int getMaxEntities();
    int getMaxErrorCode();
}
