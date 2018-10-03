package com.intelliviz.income.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.db.entity.AbstractIncomeSource;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_OWNER;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MESSAGE_MGR;

/**
 * Created by edm on 3/13/2018.
 */

public class PensionIncomeSource implements IncomeSource {
    private AbstractIncomeSource mIncomeSourceEntity;
    private MessageMgr mMessageMgr;

    public PensionIncomeSource(AbstractIncomeSource incomeSourceEntity, MessageMgr messageMgr) {
        mIncomeSourceEntity = incomeSourceEntity;
        mMessageMgr = messageMgr;
    }

    @Override
    public void startAddActivity(Context context) {
        Intent intent = new Intent(context, PensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, 0);
        intent.putExtra(EXTRA_MESSAGE_MGR, mMessageMgr);
        context.startActivity(intent);
    }

    @Override
    public void startEditActivity(Context context) {
        Intent intent = new Intent(context, PensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        intent.putExtra(EXTRA_MESSAGE_MGR, mMessageMgr);
        context.startActivity(intent);
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, PensionIncomeDetailsActivity.class);
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
