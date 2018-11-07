package com.intelliviz.db.entity;

import android.arch.persistence.room.PrimaryKey;

import com.intelliviz.data.IncomeSourceType;


/**
 * Created by edm on 10/3/2017.
 */

public abstract class IncomeSourceEntityBase implements IncomeSourceType {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int type;

    private String name;

    private int owner;

    private int included;

    public IncomeSourceEntityBase(long id, int type, String name, int owner, int included) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.owner = owner;
        this.included = included;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getOwner() {
        return owner;
    }

    @Override
    public void setOwner(int owner) {
        this.owner = owner;
    }

    @Override
    public int getIncluded() {
        return included;
    }

    @Override
    public void setIncluded(int included) {
        this.included = included;
    }
}
