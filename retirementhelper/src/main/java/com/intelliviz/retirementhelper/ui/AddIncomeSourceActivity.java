package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

public class AddIncomeSourceActivity extends AppCompatActivity {
    public static final String INCOME_TYPE = "income_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_source);
        ButterKnife.bind(this);
    }
}
