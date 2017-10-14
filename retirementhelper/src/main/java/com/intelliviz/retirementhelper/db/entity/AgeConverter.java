package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.TypeConverter;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.util.SystemUtils;

/**
 * Created by edm on 10/5/2017.
 */

public class AgeConverter {
    @TypeConverter
    public static AgeData fromString(String age) {
        return SystemUtils.parseAgeString(age);
    }

    @TypeConverter
    public static String toString(AgeData age) {
        return age.getUnformattedString();
    }
}
