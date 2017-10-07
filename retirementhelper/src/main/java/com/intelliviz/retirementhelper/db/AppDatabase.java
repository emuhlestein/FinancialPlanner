package com.intelliviz.retirementhelper.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.intelliviz.retirementhelper.db.dao.GovPensionDao;
import com.intelliviz.retirementhelper.db.dao.IncomeTypeDao;
import com.intelliviz.retirementhelper.db.dao.MilestoneAgeDao;
import com.intelliviz.retirementhelper.db.dao.MilestoneSummaryDao;
import com.intelliviz.retirementhelper.db.dao.PensionIncomeDao;
import com.intelliviz.retirementhelper.db.dao.RetirementOptionsDao;
import com.intelliviz.retirementhelper.db.dao.SavingsIncomeDao;
import com.intelliviz.retirementhelper.db.dao.SummaryDao;
import com.intelliviz.retirementhelper.db.dao.TaxDeferredIncomeDao;
import com.intelliviz.retirementhelper.db.entity.AgeConverter;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeTypeEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneSummaryEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;

/**
 * Created by edm on 10/2/2017.
 */

@Database(entities = {MilestoneAgeEntity.class, GovPensionEntity.class, IncomeTypeEntity.class,
        MilestoneSummaryEntity.class, PensionIncomeEntity.class, RetirementOptionsEntity.class,
        SavingsIncomeEntity.class, SummaryEntity.class, TaxDeferredIncomeEntity.class}, version = 1)
@TypeConverters({AgeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private volatile static AppDatabase mINSTANCE;

    public static AppDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (AppDatabase.class) {
                if(mINSTANCE == null) {
                    mINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "income_db").build();
                }
            }
        }
        return mINSTANCE;
    }

    public abstract MilestoneAgeDao milestoneAgeDao();
    public abstract GovPensionDao govPensionDao();
    public abstract IncomeTypeDao incomeTypeDao();
    public abstract PensionIncomeDao pensionIncomeDao();
    public abstract SavingsIncomeDao savingsIncomeDao();
    public abstract TaxDeferredIncomeDao taxDeferredIncomeDao();
    public abstract RetirementOptionsDao retirementOptionsDao();
    public abstract MilestoneSummaryDao milestoneSummaryDao();
    public abstract SummaryDao summaryDao();
}
