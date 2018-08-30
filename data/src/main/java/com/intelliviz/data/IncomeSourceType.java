package com.intelliviz.data;

/**
 * Created by edm on 10/3/2017.
 */

public interface IncomeSourceType {
    long getId();
    void setId(long id);
    int getType();
    void setType(int type);
    String getName();
    void setName(String name);
    int getOwner();
    void setOwner(int self);
}
