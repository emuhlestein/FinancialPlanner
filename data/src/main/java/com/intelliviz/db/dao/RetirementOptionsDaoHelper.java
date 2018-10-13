package com.intelliviz.db.dao;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.RetirementOptionsEntity;

public class RetirementOptionsDaoHelper {
    public static RetirementOptionsEntity get(AppDatabase appDatabase) {
        return appDatabase.retirementOptionsDao().get();
    }
}
