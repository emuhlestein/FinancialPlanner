package com.intelliviz.income.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.income.data.IncomeSourceViewData;
import com.intelliviz.lowlevel.data.AgeData;

import java.util.ArrayList;
import java.util.List;

public class IncomeSourceHelper {
    private List<IncomeSourceEntityBase> mIncomeList;
    private RetirementOptions mRO;

    IncomeSourceHelper(List<IncomeSourceEntityBase> incomeList, RetirementOptions ro) {
        mIncomeList = incomeList;
        mRO = ro;
    }

    public LiveData<IncomeSourceViewData> get() {
        List<AbstractIncomeSource> allSources = getAllIncomeSources(mIncomeList);
        MutableLiveData<IncomeSourceViewData> ldata = new MutableLiveData<>();
        ldata.setValue(new IncomeSourceViewData(allSources, 0, ""));
        return ldata;
    }

    private List<AbstractIncomeSource> getAllIncomeSources(List<IncomeSourceEntityBase> list) {
        AgeData endAge = mRO.getEndAge();

        List<AbstractIncomeSource> incomeSourceList = new ArrayList<>();

        List<GovPension> gpList = new ArrayList<>();
        for(IncomeSourceEntityBase entity : list) {
            if(entity instanceof GovPensionEntity) {
                GovPension gp = GovPensionEntityMapper.map((GovPensionEntity)entity);
                gpList.add(gp);
                incomeSourceList.add(gp);
            }

            if(entity instanceof PensionIncomeEntity) {
                PensionData pd = PensionDataEntityMapper.map((PensionIncomeEntity)entity);
                pd.setRules(new PensionRules(mRO.getBirthdate(), pd.getAge(), endAge, pd.getBenefit()));
                incomeSourceList.add(pd);
            }

            if(entity instanceof SavingsIncomeEntity) {
                SavingsData savingsData = SavingsDataEntityMapper.map((SavingsIncomeEntity)entity);
                savingsData.setRules(new Savings401kIncomeRules(mRO.getBirthdate(), endAge));
                incomeSourceList.add(savingsData);
            }
        }

        SocialSecurityRules.setRulesOnGovPensionEntities(gpList, mRO);

        return incomeSourceList;
    }
}
