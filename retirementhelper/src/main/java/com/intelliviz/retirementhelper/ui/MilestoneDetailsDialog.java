package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsData;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MilestoneDetailsDialog extends AppCompatActivity {
    @Bind(R.id.milestone_age_text_view) TextView mAge;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;
    @Bind(R.id.balance_text_view) TextView mBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_details_dialog);
        ButterKnife.bind(this);
    }

    private void updateUI() {
        Intent intent = getIntent();
        RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIRMENTOPTIONSDATA);
    }
}
