package com.intelliviz.db.entity;

import com.intelliviz.data.IncomeSourceData;
import com.intelliviz.data.IncomeSourceType;

public abstract class AbstractIncomeSource implements IncomeSourceData, IncomeSourceType {
    private long mId;
    private int mType;
    private String mName;
    private int mOwner;
    private int mIncluded;

    public AbstractIncomeSource(long id, int type, String name, int owner, int included) {
        mId = id;
        mType = type;
        mName = name;
        mOwner = owner;
        mIncluded = included;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public int getOwner() {
        return mOwner;
    }

    @Override
    public void setOwner(int owner) {
        mOwner = owner;
    }

    @Override
    public int getIncluded() {
        return mIncluded;
    }

    @Override
    public void setIncluded(int included) {
        mIncluded = included;
    }
}
