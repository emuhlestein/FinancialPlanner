package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;

public abstract class AbstractIncomeDataAccessor implements IncomeDataAccessor {
    private int mOwner;

    public AbstractIncomeDataAccessor(int owner) {
        mOwner = owner;
    }

    protected AgeData getOwnerAge(AgeData age, RetirementOptions ro) {
        if(mOwner == OWNER_SPOUSE) {
            return AgeUtils.getAge(ro.getSpouseBirthdate(), ro.getPrimaryBirthdate(), age);
        } else {
            return age;
        }
    }
}
