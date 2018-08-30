package com.intelliviz.income.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_OWNER;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;


/**
 * Created by edm on 3/12/2018.
 */

public class GovPensionIncomeSource implements IncomeSource {
    private AbstractIncomeSource mIncomeSourceEntity;
    public GovPensionIncomeSource(AbstractIncomeSource incomeSourceEntity) {
        mIncomeSourceEntity = incomeSourceEntity;
    }

    @Override
    public void startAddActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, GovPensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
        intent.putExtra(EXTRA_INCOME_OWNER, mIncomeSourceEntity.getOwner());
        activity.startActivity(intent);
    }

    @Override
    public void startEditActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, GovPensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_EDIT);
        activity.startActivity(intent);
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, GovPensionIncomeDetailsActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        context.startActivity(intent);
    }

    @Override
    public AbstractIncomeSource getIncomeSourceEntity() {
        return mIncomeSourceEntity;
    }

    @Override
    public long getId() {
        return mIncomeSourceEntity.getId();
    }
}
