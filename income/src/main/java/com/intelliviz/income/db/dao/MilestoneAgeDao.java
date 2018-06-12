package com.intelliviz.income.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.intelliviz.income.db.entity.MilestoneAgeEntity;

import java.util.List;

/**
 * Created by edm on 10/4/2017.
 */
@Dao
public interface MilestoneAgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MilestoneAgeEntity age);

    @Query("SELECT * FROM " + MilestoneAgeEntity.TABLE_NAME)
    List<MilestoneAgeEntity> getAges();

    @Delete
    void delete(MilestoneAgeEntity age);
}
