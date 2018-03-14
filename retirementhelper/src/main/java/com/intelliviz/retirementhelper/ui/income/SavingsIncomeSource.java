package com.intelliviz.retirementhelper.ui.income;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_TYPE;

/**
 * Created by edm on 3/13/2018.
 */

public class SavingsIncomeSource implements IncomeSource {
    private IncomeSourceEntityBase mIncomeSourceEntity;

    public SavingsIncomeSource(IncomeSourceEntityBase incomeSourceEntity) {
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
    public IncomeSourceEntityBase getIncomeSourceEntity() {
        return mIncomeSourceEntity;
    }

    @Override
    public long getId() {
        return mIncomeSourceEntity.getId();
    }
}
