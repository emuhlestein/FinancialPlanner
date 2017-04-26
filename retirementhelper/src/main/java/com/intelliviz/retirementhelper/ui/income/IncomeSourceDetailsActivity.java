package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import static com.intelliviz.retirementhelper.ui.income.AddIncomeSourceActivity.INCOME_TYPE;

/**
 * Activity to handle all income types. Currently: savings, pension, government pension.
 * The activity, using fragments:
 *  1) Displays the current parameters for the selected income type.
 *  2) Allow the parameters to be edited.
 *  3) Allow the income type to be deleted.
 *  4) Manage balances for the income type, if applicable.
 * @@author Ed Muhlestein
 */
public class IncomeSourceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_source_details);

        Intent intent = getIntent();
        int incomeType = intent.getIntExtra(INCOME_TYPE, 0);
        switch(incomeType) {
            case RetirementConstants.INCOME_TYPE_SAVINGS:
                //setContentView(R.layout.activity_add_savings_source);
                break;
            case RetirementConstants.INCOME_TYPE_PENSION:
                //setContentView(R.layout.activity_add_pension_source);
                break;
            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                setContentView(R.layout.activity_add_gov_pension_source);
                break;
            default:
                incomeType = RetirementConstants.INCOME_TYPE_SAVINGS;
                //setContentView(R.layout.activity_add_savings_source);
        }

        long incomeSourceId = intent.getLongExtra(IncomeSourceListFragment.EXTRA_INCOME_SOURCE_ID, -1);

        // if incomeSourceId is -1, this is for adding. If it is not -1, this is for editing,
        // deleting, or viewing.

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        if(incomeSourceId == -1) {
            // need to show add income source fragment
            fragment = fm.findFragmentByTag(EditIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditIncomeSourceFragment.newInstance();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditIncomeSourceFragment.EDIT_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            // need to show view income source fragment
            fragment = fm.findFragmentByTag(ViewIncomeSourceFragment.VIEW_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewIncomeSourceFragment.newInstance(incomeSourceId);
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewIncomeSourceFragment.VIEW_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }
}
