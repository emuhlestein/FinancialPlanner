package com.intelliviz.db.dao;

import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.PensionIncomeEntity;

import java.util.List;

public class PensionIncomeDaoHelper {
    public static PensionIncomeEntity getPensionIncomeEntity(AppDatabase appDatabase, long id) {
        return appDatabase.pensionIncomeDao().get(id);
    }

    public static List<PensionIncomeEntity> getAllPensionIncomeEntities(AppDatabase appDatabase) {
        return appDatabase.pensionIncomeDao().get();
    }

    public static long insert(AppDatabase appDatabase, PensionIncomeEntity pensionIncomeEntity) {
        return appDatabase.pensionIncomeDao().insert(pensionIncomeEntity);
    }

    public static void update(AppDatabase appDatabase, PensionIncomeEntity pensionIncomeEntity) {
        appDatabase.pensionIncomeDao().update(pensionIncomeEntity);
    }

    public static void delete(AppDatabase appDatabase, PensionIncomeEntity pensionIncomeEntity) {
        appDatabase.pensionIncomeDao().delete(pensionIncomeEntity);
    }
}
