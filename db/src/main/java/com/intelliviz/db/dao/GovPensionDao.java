package com.intelliviz.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.db.entity.GovPensionEntity;

import java.util.List;


/**
 * Created by edm on 10/2/2017.
 */

@Dao
public interface GovPensionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GovPensionEntity govPension);

    @Query("SELECT * FROM " + GovPensionEntity.TABLE_NAME
            + " WHERE " + GovPensionEntity.TABLE_NAME + ".id = :id")
    GovPensionEntity get(long id);

    @Query("SELECT * FROM " + GovPensionEntity.TABLE_NAME)
    List<GovPensionEntity> get();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(GovPensionEntity govPension);

    @Delete
    int delete(GovPensionEntity govPension);
}
