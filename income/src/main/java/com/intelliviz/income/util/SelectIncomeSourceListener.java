package com.intelliviz.income.util;

import com.intelliviz.db.entity.AbstractIncomeSource;

/**
 * Income source listener.
 * Created by edm on 4/22/2017.
 */
public interface SelectIncomeSourceListener {
    void onSelectIncomeSource(AbstractIncomeSource incomeSource, boolean showMenu);
}
