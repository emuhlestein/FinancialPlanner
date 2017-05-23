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
    //private long mIncomeSourceId;
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
        //mIncomeSourceId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
        mIncomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, RetirementConstants.INCOME_TYPE_SAVINGS);
        mIncomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);

        if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_ADD) {
            // Add a new income source
            ab.setSubtitle("Add income source");
            switch (mIncomeSourceType) {
                case RetirementConstants.INCOME_TYPE_SAVINGS:
                    addSavingsIncomeSourceFragment(false, intent);
                    break;
                case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                    addTaxDeferredIncomeSourceFragmnet(false);
                    break;
            }
        } else {
            // View or edit an income source

            if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                ab.setSubtitle("Add income source");
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        addSavingsIncomeSourceFragment(false, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        addTaxDeferredIncomeSourceFragmnet(false);
                        break;
                }
            } else {
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        addSavingsIncomeSourceFragment(true, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        addTaxDeferredIncomeSourceFragmnet(true);
                        break;
                }
            }
        }
    }

    private void addSavingsIncomeSourceFragment(boolean viewMode, Intent intent) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewSavingsIncomeFragment.VIEW_SAVINGS_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewSavingsIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewSavingsIncomeFragment.VIEW_SAVINGS_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditSavingsIncomeFragment.EDIT_SAVINGS_INCOME_FRAG_TAG);
            if (fragment == null) {
                /*
                SavingsIncomeData sid = null;
                if(mIncomeSourceId == -1) {
                    sid = new SavingsIncomeData(mIncomeSourceId, "", RetirementConstants.INCOME_TYPE_SAVINGS);
                } else {
                    sid = DataBaseUtils.getSavingsIncomeData(this, mIncomeSourceId);
                }
                */
                fragment = EditSavingsIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditSavingsIncomeFragment.EDIT_SAVINGS_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }

    private void addTaxDeferredIncomeSourceFragmnet(boolean viewMode) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewTaxDeferredIncomeFragment.VIEW_TAXDEF_INCOME_FRAG_TAG);
            if (fragment == null) {
                //fragment = ViewTaxDeferredIncomeFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewTaxDeferredIncomeFragment.VIEW_TAXDEF_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditTaxDeferredIncomeFragment.EDIT_TAXDEF_INCOME_FRAG_TAG);
            if (fragment == null) {
                //fragment = EditTaxDeferredIncomeFragment.newInstance(mIncomeSourceId);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditTaxDeferredIncomeFragment.EDIT_TAXDEF_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }
}
