package com.intelliviz.retirementhelper.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.retirementhelper.db.entity.SummaryEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */
@Dao
public interface SummaryDao {
    @Insert(onConflict = REPLACE)
    long insert(SummaryEntity summary);

    @Query("SELECT * FROM " + SummaryEntity.TABLE_NAME)
    List<SummaryEntity> get();

    @Update(onConflict = REPLACE)
    void update(SummaryEntity summary);

    @Query("DELETE FROM " + SummaryEntity.TABLE_NAME)
    void deleteAll();
}
