package com.intelliviz.income.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.income.db.entity.RetirementOptionsEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface RetirementOptionsDao {
    @Insert(onConflict = REPLACE)
    long insert(RetirementOptionsEntity rod);

    @Query("SELECT * FROM " + RetirementOptionsEntity.TABLE_NAME)
    RetirementOptionsEntity get();

    @Update(onConflict = REPLACE)
    void update(RetirementOptionsEntity rod);

    @Delete
    void delete(RetirementOptionsEntity rod);
}
