package com.intelliviz.retirementhelper.ui.income;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Created by edm on 3/12/2018.
 */

public class GovPensionIncomeSource implements IncomeSource {
    private IncomeSourceEntityBase mIncomeSourceEntity;
    public GovPensionIncomeSource(IncomeSourceEntityBase incomeSourceEntity) {
        mIncomeSourceEntity = incomeSourceEntity;
    }

    @Override
    public void startAddActivity(FragmentActivity activity) {
        new StartGovPensionEditActivityTask(activity, 0, RetirementConstants.INCOME_ACTION_ADD).execute();
    }

    @Override
    public void startEditActivity(FragmentActivity activity) {
        new StartGovPensionEditActivityTask(activity, mIncomeSourceEntity.getId(), RetirementConstants.INCOME_ACTION_EDIT).execute();
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, GovPensionIncomeDetailsActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        context.startActivity(intent);
    }

    @Override
    public IncomeSourceEntityBase getIncomeSourceEntity() {
        return mIncomeSourceEntity;
    }

    @Override
    public long getId() {
        return mIncomeSourceEntity.getId();
    }
}
