package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;

import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import static com.intelliviz.retirementhelper.util.RetirementConstants.TRANS_TYPE_MILESTONE_SUMMARY;


public class MilestoneSummaryIntentService extends IntentService {

    public MilestoneSummaryIntentService() {
        super("MilestoneSummaryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATING,
                    RetirementContract.TransactionStatusEntry.ACTION_INSERT, "", TRANS_TYPE_MILESTONE_SUMMARY);
            DataBaseUtils.updateMilestoneSummary(this);
            DataBaseUtils.updateStatus(this, RetirementContract.TransactionStatusEntry.STATUS_UPDATED,
                    RetirementContract.TransactionStatusEntry.ACTION_INSERT, "", TRANS_TYPE_MILESTONE_SUMMARY);
        }
    }
}
