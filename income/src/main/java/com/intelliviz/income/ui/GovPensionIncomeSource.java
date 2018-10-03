package com.intelliviz.income.ui;

import android.content.Context;
import android.content.Intent;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MESSAGE_MGR;


/**
 * Created by edm on 3/12/2018.
 */

public class GovPensionIncomeSource implements IncomeSource {
    private AbstractIncomeSource mIncomeSourceEntity;
    private MessageMgr mMessageMgr;
    public GovPensionIncomeSource(AbstractIncomeSource incomeSourceEntity, MessageMgr messageMgr) {
        mIncomeSourceEntity = incomeSourceEntity;
        mMessageMgr = messageMgr;
    }

    @Override
    public void startAddActivity(Context context) {
        Intent intent = new Intent(context, GovPensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
        intent.putExtra(EXTRA_MESSAGE_MGR, mMessageMgr);
        context.startActivity(intent);
    }

    @Override
    public void startEditActivity(Context context) {
        Intent intent = new Intent(context, GovPensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_EDIT);
        intent.putExtra(EXTRA_MESSAGE_MGR, mMessageMgr);
        context.startActivity(intent);
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, GovPensionIncomeDetailsActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_MESSAGE_MGR, mMessageMgr);
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
