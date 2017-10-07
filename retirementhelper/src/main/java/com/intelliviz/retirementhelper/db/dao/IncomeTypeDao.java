package com.intelliviz.retirementhelper.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.retirementhelper.db.entity.IncomeTypeEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface IncomeTypeDao {
    @Insert(onConflict = REPLACE)
    long insert(IncomeTypeEntity income);

    @Query("SELECT * FROM " + IncomeTypeEntity.TABLE_NAME + " WHERE id = :id")
    LiveData<IncomeTypeEntity> get(long id);

    @Update(onConflict = REPLACE)
    void update(IncomeTypeEntity income);

    @Delete
    void delete(IncomeTypeEntity income);
}
