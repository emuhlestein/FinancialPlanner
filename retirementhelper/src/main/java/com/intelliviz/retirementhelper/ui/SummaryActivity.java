package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.ui.income.IncomeSourceActivity;
import com.intelliviz.retirementhelper.ui.income.IncomeSourceListFragment;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

public class SummaryActivity extends AppCompatActivity {
    private static final String DIALOG_RETIRE_OPTIONS = "reitre_options";
    private static final String SUMMARY_FRAG_TAG = "summary frag tag";
    private static final String EXPENSES_FRAG_TAG = "expenses frag tag";
    private static final String INCOME_FRAG_TAG = "income frag tag";
    private static final String TAXES_FRAG_TAG = "taxes frag tag";
    private static final String MILESTONES_FRAG_TAG = "milestones frag tag";

    @Bind(R.id.summary_toolbar) Toolbar mToolbar;
    @Bind(R.id.bottom_navigation) BottomNavigationView mBottonNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        fragment = fm.findFragmentByTag(SUMMARY_FRAG_TAG);
        if (fragment == null) {

            RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(this);
            Intent intent = new Intent(this, IncomeSourceActivity.class);
            intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
            fragment = SummaryFragment.newInstance(intent);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.content_frame, fragment, SUMMARY_FRAG_TAG);
            ft.commit();
        }

        mBottonNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = null;
                FragmentTransaction ft = null;
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(SummaryActivity.this);
                        Intent intent = new Intent(SummaryActivity.this, IncomeSourceActivity.class);
                        intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                        fragment = SummaryFragment.newInstance(intent);
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, SUMMARY_FRAG_TAG);
                        ft.commit();
                        break;
                    case R.id.expenses_menu:
                        fragment = ExpensesFragment.newInstance();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, EXPENSES_FRAG_TAG);
                        ft.commit();
                        break;
                    case R.id.income_menu:
                        fragment = IncomeSourceListFragment.newInstance();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, INCOME_FRAG_TAG);
                        ft.commit();
                        break;
                    case R.id.taxes_menu:
                        fragment = TaxesFragment.newInstance();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, TAXES_FRAG_TAG);
                        ft.commit();
                        break;
                    case R.id.milestones_menu:
                        fragment = MilestonesFragment.newInstance();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, MILESTONES_FRAG_TAG);
                        ft.commit();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
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
                RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(this);
                if (rod != null) {
                    intent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
                }
                startActivityForResult(intent, REQUEST_RETIRE_OPTIONS);

                break;
            case R.id.personal_info_item:
                intent = new Intent(this, PersonalInfoDialog.class);
                PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(this);
                if (pid != null) {
                    intent.putExtra(RetirementConstants.EXTRA_PERSONALINFODATA, pid);
                }
                startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(SystemUtils.onActivityResultForOptionMenu(this, requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
