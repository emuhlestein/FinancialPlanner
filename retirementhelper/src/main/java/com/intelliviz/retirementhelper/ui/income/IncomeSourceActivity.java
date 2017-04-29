package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomeSourceActivity extends AppCompatActivity {
    private long mIncomeSourceId;
    @Bind(R.id.income_source_toolbar) Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_source);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();


        Intent intent = getIntent();
        mIncomeSourceId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        if(mIncomeSourceId == -1) {
            // Add a new income source
            ab.setSubtitle("Add income source");
            fragment = fm.findFragmentByTag(AddIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = AddIncomeSourceFragment.newInstance(mIncomeSourceId);
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, AddIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            // View or edit an income source
            int incomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION,
                    RetirementConstants.INCOME_ACTION_VIEW);
            if(incomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                ab.setSubtitle("Add income source");
                fragment = fm.findFragmentByTag(AddIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
                if (fragment == null) {
                    fragment = AddIncomeSourceFragment.newInstance(mIncomeSourceId);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.content_frame, fragment, AddIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
                    ft.commit();
                }
            } else {
                fragment = fm.findFragmentByTag(ViewIncomeSourceFragment.VIEW_INCOME_FRAG_TAG);
                if (fragment == null) {
                    fragment = ViewIncomeSourceFragment.newInstance(mIncomeSourceId);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.content_frame, fragment, ViewIncomeSourceFragment.VIEW_INCOME_FRAG_TAG);
                    ft.commit();
                }
            }
        }
    }
}
