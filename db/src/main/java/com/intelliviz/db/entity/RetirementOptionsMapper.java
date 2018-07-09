package com.intelliviz.db.entity;

import com.intelliviz.data.RetirementOptions;

public class RetirementOptionsMapper {
    public static RetirementOptionsEntity map(RetirementOptions ro) {
        return new RetirementOptionsEntity(ro.getId(), ro.getEndAge(), ro.getBirthdate(), ro.getIncludeSpouse(), ro.getSpouseBirthdate(), ro.getCountryCode());
    }

    public static RetirementOptions map(RetirementOptionsEntity roe) {
        return new RetirementOptions(roe.getId(), roe.getEndAge(), roe.getBirthdate(), roe.getSpouseBirthdate(), roe.getIncludeSpouse(), roe.getCountryCode());
    }
}
