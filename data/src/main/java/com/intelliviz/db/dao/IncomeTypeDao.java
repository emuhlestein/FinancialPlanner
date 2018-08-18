package com.intelliviz.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.db.entity.IncomeTypeEntity;

/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface IncomeTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(IncomeTypeEntity income);

    @Query("SELECT * FROM " + IncomeTypeEntity.TABLE_NAME + " WHERE id = :id")
    IncomeTypeEntity get(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(IncomeTypeEntity income);

    @Delete
    void delete(IncomeTypeEntity income);
}
