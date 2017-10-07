package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;

/**
 * Income source listener.
 * Created by edm on 4/22/2017.
 */
public interface SelectIncomeSourceListener {
    void onSelectIncomeSource(IncomeSourceEntityBase incomeSource, boolean showMenu);
}
