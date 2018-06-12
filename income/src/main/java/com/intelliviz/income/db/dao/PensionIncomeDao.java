package com.intelliviz.income.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.income.db.entity.PensionIncomeEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface PensionIncomeDao {
    @Insert(onConflict = REPLACE)
    long insert(PensionIncomeEntity pension);

    @Query("SELECT * FROM " + PensionIncomeEntity.TABLE_NAME
            + " WHERE " + PensionIncomeEntity.TABLE_NAME + ".id = :id")
    PensionIncomeEntity get(long id);

    @Query("SELECT * FROM " + PensionIncomeEntity.TABLE_NAME)
    List<PensionIncomeEntity> get();

    @Update(onConflict = REPLACE)
    int update(PensionIncomeEntity pension);

    @Delete
    int delete(PensionIncomeEntity pension);
}
