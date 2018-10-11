package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.db.entity.RetirementOptionsEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */

@Entity(tableName = TABLE_NAME)
public class RetirementOptionsEntity {
    public static final String TABLE_NAME = "retirement_options";
    public static final String END_AGE_FIELD = "end_age";
    public static final String SPOUSE_END_AGE_FIELD = "spouse_end_age";
    public static final String BIRTHDATE_FIELD = "birthdate";
    public static final String INCLUDE_SPOUSE_FIELD = "include_spouse";
    public static final String SPOUSE_BIRTHDATE_FIELD = "spouse_birthdate";
    public static final String COUNTRY_CODE_FIELD = "country_code";

    @PrimaryKey(autoGenerate = true)
    private long id;
    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = END_AGE_FIELD)
    private AgeData mEndAge;
    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = SPOUSE_END_AGE_FIELD)
    private AgeData mSpouseEndAge;
    @ColumnInfo(name = BIRTHDATE_FIELD)
    private String mBirthdate;
    @ColumnInfo(name = SPOUSE_BIRTHDATE_FIELD)
    private String mSpouseBirthdate;
    @ColumnInfo(name = INCLUDE_SPOUSE_FIELD)
    private int mIncludeSpouse;
    @ColumnInfo(name = COUNTRY_CODE_FIELD)
    private String mCountryCode;

    public RetirementOptionsEntity(long id, AgeData endAge, AgeData spouseEndAge, String birthdate, int includeSpouse, String spouseBirthdate, String countryCode) {
        this.id = id;
        mEndAge = endAge;
        mSpouseEndAge = spouseEndAge;
        mBirthdate = birthdate;
        mIncludeSpouse = includeSpouse;
        mSpouseBirthdate = spouseBirthdate;
        mCountryCode = countryCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AgeData getEndAge() {
        return mEndAge;
    }

    public void setEndAge(AgeData endAge) {
        mEndAge = endAge;
    }

    public AgeData getSpouseEndAge() {
        return mSpouseEndAge;
    }

    public void setSpouseEndAge(AgeData spouseEndAge) {
        mSpouseEndAge = spouseEndAge;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public void setBirthdate(String birthdate) {
        mBirthdate = birthdate;
    }

    public String getSpouseBirthdate() {
        return mSpouseBirthdate;
    }

    public void setSpouseBirthdate(String spouseBirthdate) {
        mSpouseBirthdate = spouseBirthdate;
    }

    public int getIncludeSpouse() {
        return mIncludeSpouse;
    }

    public void setIncludeSpouse(int includeSpouse) {
        mIncludeSpouse = includeSpouse;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }
}
