package com.intelliviz.retirementhelper.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */
@Dao
public interface TaxDeferredIncomeDao {
    @Insert(onConflict = REPLACE)
    long insert(TaxDeferredIncomeEntity income);

    @Query("SELECT * FROM " + TaxDeferredIncomeEntity.TABLE_NAME
            + " WHERE " + TaxDeferredIncomeEntity.TABLE_NAME + ".id = :id")
    TaxDeferredIncomeEntity get(long id);

    @Query("SELECT * FROM " + TaxDeferredIncomeEntity.TABLE_NAME)
    List<TaxDeferredIncomeEntity> get();

    @Update(onConflict = REPLACE)
    int update(TaxDeferredIncomeEntity income);

    @Delete
    int delete(TaxDeferredIncomeEntity income);
}
