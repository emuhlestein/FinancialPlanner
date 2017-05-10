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
    private int mIncomeSourceType;
    private int mIncomeSourceAction;
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
        mIncomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, RetirementConstants.INCOME_TYPE_SAVINGS);
        mIncomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        // TODO currently only supports savings account
        // TODO need to support other accounts also, like tax deferred

        if(mIncomeSourceId == -1) {
            // Add a new income source
            ab.setSubtitle("Add income source");
            switch (mIncomeSourceType) {
                case RetirementConstants.INCOME_TYPE_SAVINGS:
                    addSavingsIncomeSourceFragmnet(false);
                    break;
                case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                    fragment = fm.findFragmentByTag(EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
                    if (fragment == null) {
                        fragment = EditTaxDeferredIncomeFragment.newInstance(mIncomeSourceId);
                        ft = fm.beginTransaction();
                        ft.add(R.id.content_frame, fragment, EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
                        ft.commit();
                    }
                    break;
            }
        } else {
            // View or edit an income source

            if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                ab.setSubtitle("Add income source");
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        fragment = fm.findFragmentByTag(EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
                        if (fragment == null) {
                            fragment = EditTaxDeferredIncomeFragment.newInstance(mIncomeSourceId);
                            ft = fm.beginTransaction();
                            ft.add(R.id.content_frame, fragment, EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
                            ft.commit();
                        }
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        addSavingsIncomeSourceFragmnet(false);
                        break;
                }
            } else {
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        addSavingsIncomeSourceFragmnet(true);
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        break;
                }
            }
        }
    }

    private void addSavingsIncomeSourceFragmnet(boolean viewMode) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewIncomeSavingsFragment.VIEW_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewIncomeSavingsFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewIncomeSavingsFragment.VIEW_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditSavingsIncomeFragment.EDIT_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditSavingsIncomeFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditSavingsIncomeFragment.EDIT_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }

    private void addTaxDeferredIncomeSourceFragmnet(boolean viewMode) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewIncomeSavingsFragment.VIEW_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewIncomeSavingsFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewIncomeSavingsFragment.VIEW_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditTaxDeferredIncomeFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditTaxDeferredIncomeFragment.TAXDEF_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }
}
