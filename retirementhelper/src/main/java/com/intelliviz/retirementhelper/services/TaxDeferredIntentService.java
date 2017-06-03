package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;

public class TaxDeferredIntentService extends IntentService {

    public TaxDeferredIntentService() {
        super("TaxDeferredIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            long incomeSourceId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            if(incomeSourceId != -1) {
                TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(this, incomeSourceId);

                if(tdid != null) {
                    int incomeSourceType = intent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, -1);
                    int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, -1);

                    Intent localIntent = new Intent(LOCAL_TAX_DEFERRED);
                    localIntent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_INCOME_DATA, tdid);
                    localIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                    localIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        }
    }
}
