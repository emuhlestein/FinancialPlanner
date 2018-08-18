package com.intelliviz.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.db.entity.PensionIncomeEntity;

import java.util.List;

/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface PensionIncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PensionIncomeEntity pension);

    @Query("SELECT * FROM " + PensionIncomeEntity.TABLE_NAME
            + " WHERE " + PensionIncomeEntity.TABLE_NAME + ".id = :id")
    PensionIncomeEntity get(long id);

    @Query("SELECT * FROM " + PensionIncomeEntity.TABLE_NAME)
    List<PensionIncomeEntity> get();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(PensionIncomeEntity pension);

    @Delete
    int delete(PensionIncomeEntity pension);
}
