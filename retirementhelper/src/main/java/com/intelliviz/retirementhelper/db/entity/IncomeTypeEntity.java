package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static com.intelliviz.retirementhelper.db.entity.IncomeTypeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */


@Entity(tableName = TABLE_NAME)
public class IncomeTypeEntity {
    public static final String TABLE_NAME = "income_type";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
