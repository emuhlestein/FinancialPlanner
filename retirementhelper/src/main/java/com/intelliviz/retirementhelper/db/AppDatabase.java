package com.intelliviz.retirementhelper.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.db.dao.GovPensionDao;
import com.intelliviz.retirementhelper.db.dao.MilestoneAgeDao;
import com.intelliviz.retirementhelper.db.dao.MilestoneSummaryDao;
import com.intelliviz.retirementhelper.db.dao.PensionIncomeDao;
import com.intelliviz.retirementhelper.db.dao.RetirementOptionsDao;
import com.intelliviz.retirementhelper.db.dao.SavingsIncomeDao;
import com.intelliviz.retirementhelper.db.dao.SummaryDao;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeTypeEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneSummaryEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;

/**
 * Created by edm on 10/2/2017.
 */

@Database(entities = {MilestoneAgeEntity.class, GovPensionEntity.class, IncomeTypeEntity.class,
        MilestoneSummaryEntity.class, PensionIncomeEntity.class, RetirementOptionsEntity.class,
        SavingsIncomeEntity.class, SummaryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private volatile static AppDatabase mINSTANCE;

    public static AppDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (AppDatabase.class) {
                if(mINSTANCE == null) {
                    RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            // set default values
                            ContentValues values = new ContentValues();
                            values.put(RetirementOptionsEntity.END_AGE_FIELD, 90*12);
                            values.put(RetirementOptionsEntity.CURRENT_OPTION_FIELD, 0);
                            values.put(RetirementOptionsEntity.REACH_AMOUNT_FIELD, "1000000");
                            values.put(RetirementOptionsEntity.REACH_PERCENT_FIELD, "80");
                            values.put(RetirementOptionsEntity.BIRTHDATE_FIELD, 0);
                            db.insert(RetirementOptionsEntity.TABLE_NAME, OnConflictStrategy.IGNORE, values);
                        }
                    };

                    mINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "income_db")
                            .addCallback(rdc)
                            .build();
                }
            }
        }
        return mINSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tax_deferred_income "
                    + " ADD COLUMN start_age TEXT");
        }
    };

    public abstract MilestoneAgeDao milestoneAgeDao();
    public abstract GovPensionDao govPensionDao();
    public abstract PensionIncomeDao pensionIncomeDao();
    public abstract SavingsIncomeDao savingsIncomeDao();
    public abstract RetirementOptionsDao retirementOptionsDao();
    public abstract MilestoneSummaryDao milestoneSummaryDao();
    public abstract SummaryDao summaryDao();
}
