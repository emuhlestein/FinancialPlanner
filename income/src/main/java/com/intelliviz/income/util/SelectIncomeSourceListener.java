package com.intelliviz.income.util;

import com.intelliviz.income.db.entity.IncomeSourceEntityBase;

/**
 * Income source listener.
 * Created by edm on 4/22/2017.
 */
public interface SelectIncomeSourceListener {
    void onSelectIncomeSource(IncomeSourceEntityBase incomeSource, boolean showMenu);
}
