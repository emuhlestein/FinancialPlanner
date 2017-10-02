package com.intelliviz.retirementhelper.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.intelliviz.retirementhelper.db.entity.AgeEntity;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeTypeEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneSummaryEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;

/**
 * Created by edm on 10/2/2017.
 */

@Database(entities = {AgeEntity.class, GovPensionEntity.class, IncomeTypeEntity.class,
        MilestoneSummaryEntity.class, PensionIncomeEntity.class, RetirementOptionsEntity.class,
        SavingsIncomeEntity.class, SummaryEntity.class, TaxDeferredIncomeEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
}
