package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaxDeferredIncomeActivity extends AppCompatActivity {

    @Bind(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_deferred_income);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        int mIncomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, RetirementConstants.INCOME_TYPE_SAVINGS);
        int mIncomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);

        if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_ADD) {
            addTaxDeferredIncomeSourceFragment(false, intent);
        } else {
            // View or edit an income source
            if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                // View or edit an income source
                addTaxDeferredIncomeSourceFragment(false, intent);
            } else {
                addTaxDeferredIncomeSourceFragment(true, intent);
            }
        }
    }
    private void addTaxDeferredIncomeSourceFragment(boolean viewMode, Intent intent) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewTaxDeferredIncomeFragment.VIEW_TAXDEF_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewTaxDeferredIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewTaxDeferredIncomeFragment.VIEW_TAXDEF_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditTaxDeferredIncomeFragment.EDIT_TAXDEF_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditTaxDeferredIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditTaxDeferredIncomeFragment.EDIT_TAXDEF_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }
}
