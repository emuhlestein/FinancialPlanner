package com.intelliviz.income.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.db.entity.AbstractIncomeSource;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_OWNER;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_TYPE;


/**
 * Created by edm on 3/13/2018.
 */

public class SavingsIncomeSource implements IncomeSource {
    private AbstractIncomeSource mIncomeSourceEntity;

    public SavingsIncomeSource(AbstractIncomeSource incomeSourceEntity) {
        mIncomeSourceEntity = incomeSourceEntity;
    }

    @Override
    public void startAddActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, SavingsIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, 0);
        intent.putExtra(EXTRA_INCOME_TYPE, mIncomeSourceEntity.getType());
        activity.startActivity(intent);
    }

    @Override
    public void startEditActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, SavingsIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_INCOME_TYPE, mIncomeSourceEntity.getType());
        activity.startActivity(intent);
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, SavingsIncomeDetailsActivity.class);
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
