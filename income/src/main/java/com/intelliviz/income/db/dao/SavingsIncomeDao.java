package com.intelliviz.income.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.income.db.entity.SavingsIncomeEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */
@Dao
public interface SavingsIncomeDao {
    @Insert(onConflict = REPLACE)
    long insert(SavingsIncomeEntity savings);

    @Query("SELECT * FROM " + SavingsIncomeEntity.TABLE_NAME
            + " WHERE " + SavingsIncomeEntity.TABLE_NAME + ".id = :id")
    SavingsIncomeEntity get(long id);

    @Query("SELECT * FROM " + SavingsIncomeEntity.TABLE_NAME)
    List<SavingsIncomeEntity> get();

    @Update(onConflict = REPLACE)
    int update(SavingsIncomeEntity savings);

    @Delete
    int delete(SavingsIncomeEntity savings);
}
