package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.ui.PersonalInfoDialog;
import com.intelliviz.retirementhelper.ui.RetirementOptionsDialog;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

public class IncomeSourceActivity extends AppCompatActivity {
    @Bind(R.id.income_source_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_source);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        int mIncomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, RetirementConstants.INCOME_TYPE_SAVINGS);
        int mIncomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);

        if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_ADD) {
            // Add a new income source
            switch (mIncomeSourceType) {
                case RetirementConstants.INCOME_TYPE_SAVINGS:
                    addSavingsIncomeSourceFragment(false, intent);
                    break;
                case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                    addTaxDeferredIncomeSourceFragmnet(false, intent);
                    break;
                case RetirementConstants.INCOME_TYPE_PENSION:
                    addPensionIncomeSourceFragmnet(false, intent);
                    break;
                case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                    addGovPensionIncomeSourceFragmnet(false, intent);
                    break;
            }
        } else {
            // View or edit an income source
            if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        addSavingsIncomeSourceFragment(false, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        addTaxDeferredIncomeSourceFragmnet(false, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_PENSION:
                        addPensionIncomeSourceFragmnet(false, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                        addGovPensionIncomeSourceFragmnet(false, intent);
                        break;
                }
            } else {
                switch (mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        addSavingsIncomeSourceFragment(true, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        addTaxDeferredIncomeSourceFragmnet(true, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_PENSION:
                        addPensionIncomeSourceFragmnet(true, intent);
                        break;
                    case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                        addGovPensionIncomeSourceFragmnet(true, intent);
                        break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.retirement_options_item:
                intent = new Intent(this, RetirementOptionsDialog.class);
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (rod != null) {
                    intent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
                }
                startActivityForResult(intent, REQUEST_RETIRE_OPTIONS);

                break;
            case R.id.personal_info_item:
                intent = new Intent(this, PersonalInfoDialog.class);
                rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (rod != null) {
                    intent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
                }
                startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                fragment = EditSavingsIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditSavingsIncomeFragment.EDIT_SAVINGS_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }

    private void addTaxDeferredIncomeSourceFragmnet(boolean viewMode, Intent intent) {
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

    private void addPensionIncomeSourceFragmnet(boolean viewMode, Intent intent) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewPensionIncomeFragment.VIEW_PENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewPensionIncomeFragment.VIEW_PENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditPensionIncomeFragment.EDIT_PENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditPensionIncomeFragment.EDIT_PENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }

    private void addGovPensionIncomeSourceFragmnet(boolean viewMode, Intent intent) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewGovPensionIncomeFragment.VIEW_GOV_PENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewGovPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewGovPensionIncomeFragment.VIEW_GOV_PENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditGovPensionIncomeFragment.EDIT_GOVPENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditGovPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditGovPensionIncomeFragment.EDIT_GOVPENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(SystemUtils.onActivityResultForOptionMenu(this, requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
