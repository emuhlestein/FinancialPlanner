package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class IncomeSourceData {
    private final String name;
    private final int type;
    public IncomeSourceData(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
