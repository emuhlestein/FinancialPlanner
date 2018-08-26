package com.intelliviz.data;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;

import java.util.List;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionData extends AbstractIncomeSource {
    private AgeData mAge;
    private String mBenefit;
    private int mBenefitInfo;
    private PensionRules mRules;

    public PensionData(long id, int type, int self) {
        this(id, type, "", self);
    }

    public PensionData(long id, int type, String name, int self) {
        super(id, type, name, self);
    }

    public PensionData(long id, int type, String name, int self,
                       AgeData age, String benefit, int benefitInfo) {
        this(id, type, name, self);
        mAge = age;
        mBenefit = benefit;
        mBenefitInfo = benefitInfo;
    }

    public void setAge(AgeData age) {
        mAge = age;
    }

    public void setBenefit(String benefit) {
        mBenefit = benefit;
    }

    public AgeData getAge() {
        return mAge;
    }

    public String getBenefit() {
        return mBenefit;
    }

    public int getBenefitInfo() {
        return mBenefitInfo;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof PensionRules) {
            mRules = (PensionRules)rules;
        } else {
            mRules = null;
        }
    }

    @Override
    public List<IncomeData> getIncomeData() {
        if(mRules != null) {
            return mRules.getIncomeData();
        } else {
            return null;
        }
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        if(mRules != null) {
            return mRules.getIncomeDataAccessor();
        } else {
            return null;
        }
    }
}
