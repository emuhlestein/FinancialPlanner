package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.retirementhelper.data.AgeData;

import static com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */

@Entity(tableName = TABLE_NAME)
public class RetirementOptionsEntity {
    public static final String TABLE_NAME = "retirement_options";
    public static final String END_AGE_FIELD = "end_age";
    public static final String BIRTHDATE_FIELD = "birthdate";

    @PrimaryKey(autoGenerate = true)
    private int id;
    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = END_AGE_FIELD)
    private AgeData endAge;
    @ColumnInfo(name = BIRTHDATE_FIELD)
    private String birthdate;

    public RetirementOptionsEntity(int id, AgeData endAge, String birthdate) {
        this.id = id;
        this.endAge = endAge;
        this.birthdate = birthdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AgeData getEndAge() {
        return endAge;
    }

    public void setEndAge(AgeData endAge) {
        this.endAge = endAge;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
