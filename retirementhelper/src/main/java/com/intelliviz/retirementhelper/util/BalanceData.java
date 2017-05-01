package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class BalanceData {
    private final String balance;
    private final String date;

    public BalanceData(String balance, String date) {
        this.balance = balance;
        this.date = date;
    }

    public String getBalance() {
        return balance;
    }
}
