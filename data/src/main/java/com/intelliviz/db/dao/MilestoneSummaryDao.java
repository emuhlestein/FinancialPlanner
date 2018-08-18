package com.intelliviz.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.db.entity.MilestoneSummaryEntity;

import java.util.List;

/**
 * Created by edm on 10/2/2017.
 */
@Dao
public interface MilestoneSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MilestoneSummaryEntity milestoneSummaryEntity);

    @Query("SELECT * FROM " + MilestoneSummaryEntity.TABLE_NAME + " WHERE id = :id")
    MilestoneSummaryEntity get(long id);

    @Query("SELECT * FROM " + MilestoneSummaryEntity.TABLE_NAME)
    List<MilestoneSummaryEntity> getAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(MilestoneSummaryEntity milestoneSummaryEntity);

    @Delete
    void delete(MilestoneSummaryEntity milestoneSummaryEntity);

    @Query("DELETE FROM " + MilestoneSummaryEntity.TABLE_NAME)
    void deleteAll();
}
