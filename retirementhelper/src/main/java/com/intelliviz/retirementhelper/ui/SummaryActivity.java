package com.intelliviz.retirementhelper.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.ui.income.IncomeSourceListFragment;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementInfoMgr;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.widget.WidgetProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_PERSONALINFODATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

public class SummaryActivity extends AppCompatActivity {
    private static final String DIALOG_RETIRE_OPTIONS = "retire_options";
    private static final String SUMMARY_FRAG_TAG = "summary frag tag";
    private static final String EXPENSES_FRAG_TAG = "expenses frag tag";
    private static final String INCOME_FRAG_TAG = "income frag tag";
    private static final String TAXES_FRAG_TAG = "taxes frag tag";
    private static final String MILESTONES_FRAG_TAG = "milestones frag tag";
    private GoogleApiClient mGoogleApiClient;
    private boolean mNeedToStartSummaryFtagment;

    @Bind(R.id.summary_toolbar) Toolbar mToolbar;
    @Bind(R.id.bottom_navigation) BottomNavigationView mBottonNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        String birthdate = RetirementInfoMgr.getInstance().getBirthdate();
        if(!SystemUtils.validateBirthday(birthdate)) {
            Intent intent = new Intent(this, PersonalInfoDialog.class);
            PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(this);
            if (pid != null) {
                intent.putExtra(EXTRA_PERSONALINFODATA, pid);
            }
            startActivityForResult(intent, REQUEST_PERSONAL_INFO);
            mNeedToStartSummaryFtagment = true;
            return;
        }

        mNeedToStartSummaryFtagment = false;

        startSummaryFragment();
        setNavigationFragment();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName appWidget = new ComponentName(this, WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
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
                    intent.putExtra(EXTRA_PERSONALINFODATA, pid);
                }
                startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                break;
            case R.id.sign_out_item:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                FirebaseAuth  auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                mGoogleApiClient.disconnect();
                                Intent intent = new Intent(SummaryActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
            case R.id.revoke_item:
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                FirebaseAuth  auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                mGoogleApiClient.disconnect();
                                Intent intent = new Intent(SummaryActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mNeedToStartSummaryFtagment) {
            // fragment transactions have to be handled outside of onActivityResult.
            // The state has already been saved and no state modifications are allowed.
            startSummaryFragment();
            setNavigationFragment();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_RETIRE_OPTIONS:
                if (resultCode == RESULT_OK) {
                    RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
                    SystemUtils.updateROD(this, rod);
                }
                break;
            case REQUEST_PERSONAL_INFO:
                if (resultCode == RESULT_OK) {
                    PersonalInfoData pid = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);
                    SystemUtils.updatePERID(this, pid);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void startSummaryFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        fragment = fm.findFragmentByTag(SUMMARY_FRAG_TAG);
        if (fragment == null) {

            RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(this);
            PersonalInfoData perid = DataBaseUtils.getPersonalInfoData(this);
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_RETIREOPTIONS_DATA, rod);
            bundle.putParcelable(EXTRA_PERSONALINFODATA, perid);
            fragment = SummaryFragment.newInstance(bundle);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.content_frame, fragment, SUMMARY_FRAG_TAG);
            ft.commit();
        }
    }

    private void setNavigationFragment() {
        mBottonNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = null;
                FragmentTransaction ft = null;
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(SummaryActivity.this);
                        PersonalInfoData perid = DataBaseUtils.getPersonalInfoData(SummaryActivity.this);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EXTRA_RETIREOPTIONS_DATA, rod);
                        bundle.putParcelable(EXTRA_PERSONALINFODATA, perid);
                        fragment = SummaryFragment.newInstance(bundle);
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, SUMMARY_FRAG_TAG);
                        ft.commit();
                        break;
                    case R.id.income_menu:
                        fragment = IncomeSourceListFragment.newInstance();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, INCOME_FRAG_TAG);
                        ft.commit();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
