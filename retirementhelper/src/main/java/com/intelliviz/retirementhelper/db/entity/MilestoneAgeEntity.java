package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;

import static com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity.TABLE_NAME;

/**
 * Created by edm on 10/5/2017.
 */

@Entity(tableName = TABLE_NAME)
public class MilestoneAgeEntity implements Comparable{
    public static final String TABLE_NAME = "milestone_ages";

    @PrimaryKey(autoGenerate = true)
    private long id;

    @TypeConverters({AgeConverter.class})
    private AgeData age;

    public MilestoneAgeEntity(long id, AgeData age) {
        this.id = id;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AgeData getAge() {
        return age;
    }

    public void setAge(AgeData age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(obj == this) {
            return true;
        }

        if(!(obj instanceof MilestoneAgeEntity)) {
            return false;
        }

        MilestoneAgeEntity milestoneAge = (MilestoneAgeEntity)obj;

        return age.equals(milestoneAge.age);
    }

    @Override
    public int hashCode() {
        return age.hashCode();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        MilestoneAgeEntity mad = (MilestoneAgeEntity)o;
        return age.getNumberOfMonths()-mad.getAge().getNumberOfMonths();
    }
}
