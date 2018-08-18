package com.intelliviz.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.db.entity.SummaryEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class SummaryEntity {
    public static final String TABLE_NAME = "summary";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @TypeConverters({AgeConverter.class})
    private AgeData age;

    private String amount;

    public SummaryEntity(int id, AgeData age, String amount) {
        this.id = id;
        this.age = age;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AgeData getAge() {
        return age;
    }

    public void setAge(AgeData age) {
        this.age = age;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
