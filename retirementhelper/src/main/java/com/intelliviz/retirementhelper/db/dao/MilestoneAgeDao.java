package com.intelliviz.retirementhelper.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/4/2017.
 */
@Dao
public interface MilestoneAgeDao {
    @Insert(onConflict = REPLACE)
    long insert(MilestoneAgeEntity age);

    @Query("SELECT * FROM " + MilestoneAgeEntity.TABLE_NAME)
    List<MilestoneAgeEntity> getAges();

    @Delete
    void delete(MilestoneAgeEntity age);
}
