package com.intelliviz.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static com.intelliviz.db.entity.IncomeTypeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */


@Entity(tableName = TABLE_NAME)
public class IncomeTypeEntity {
    public static final String TABLE_NAME = "income_type";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private int type;
    private String name;

    public IncomeTypeEntity(long id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
