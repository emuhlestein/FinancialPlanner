package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.PrimaryKey;

import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.IncomeSourceType;

import java.util.List;

/**
 * Created by edm on 10/3/2017.
 */

public abstract class IncomeSourceEntityBase implements IncomeSourceType {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int type;

    private String name;

    public IncomeSourceEntityBase(long id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
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

    public abstract List<BenefitData> getBenefitData();
}
