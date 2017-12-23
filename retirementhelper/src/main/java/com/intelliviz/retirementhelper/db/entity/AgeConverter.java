package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.TypeConverter;

import com.intelliviz.retirementhelper.data.AgeData;

/**
 * Created by edm on 10/5/2017.
 */

public class AgeConverter {
    @TypeConverter
    public static AgeData fromInteger(int age) {
        return new AgeData(age);
    }

    @TypeConverter
    public static int toInteger(AgeData age) {
        return age.getNumberOfMonths();
    }
}
