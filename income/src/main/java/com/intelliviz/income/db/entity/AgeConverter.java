package com.intelliviz.income.db.entity;

import android.arch.persistence.room.TypeConverter;

import com.intelliviz.income.data.AgeData;

/**
 * Created by edm on 10/5/2017.
 */

public class AgeConverter {
    @TypeConverter
    public static AgeData fromInteger(int numMonths) {
        return new AgeData(numMonths);
    }

    @TypeConverter
    public static int toInteger(AgeData age) {
        return age.getNumberOfMonths();
    }
}
