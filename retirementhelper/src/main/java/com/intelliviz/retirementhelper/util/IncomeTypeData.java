package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/12/2017.
 */

public class IncomeTypeData {
    private final String name;
    private final int type;

    public IncomeTypeData(String name, int type) {
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
