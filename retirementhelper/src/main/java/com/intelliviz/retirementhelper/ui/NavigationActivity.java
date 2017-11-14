package com.intelliviz.retirementhelper.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.ui.income.IncomeSourceListFragment;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.viewmodel.NavigationModelView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

/**
 * The summary activity.
 * @author Ed Muhlestein
 */
public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private static final String SUMMARY_FRAG_TAG = "summary frag tag";
    private static final String INCOME_FRAG_TAG = "income frag tag";
    private static final String MILESTONES_FRAG_TAG = "milestones frag tag";
    private GoogleApiClient mGoogleApiClient;
    private boolean mNeedToStartSummaryFragment;
    private int mStartFragment;
    private int mPrevFragment;
    private NavigationModelView mViewModel;
    private RetirementOptionsEntity mROM;

    @BindView(R.id.summary_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mNeedToStartSummaryFragment = true;

        initBottomNavigation();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null) {
            MenuItem selectedItem;
            selectedItem = mBottomNavigation.getMenu().getItem(0);
            selectedNavFragment(selectedItem);
        }

        mViewModel = ViewModelProviders.of(this).get(NavigationModelView.class);

        mViewModel.getROM().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity rom) {
                mROM = rom;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("START_FRAGMENT", mStartFragment);
        super.onSaveInstanceState(outState);
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
        RetirementOptionsData rod;
        switch (item.getItemId()) {
            case R.id.retirement_options_item:
                intent = new Intent(this, RetirementOptionsDialog.class);
                Bundle b = new Bundle();
                rod = new RetirementOptionsData(mROM.getEndAge(), mROM.getWithdrawMode(), mROM.getWithdrawAmount(), mROM.getBirthdate(), mROM.getPercentIncrease());
                AgeData age = new AgeData(2,3);
                b.putParcelable(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
                b.putParcelable("test1", age);
                intent.putExtras(b);
                startActivityForResult(intent, REQUEST_RETIRE_OPTIONS);
                overridePendingTransition(R.anim.slide_right_in, 0);
                break;
            case R.id.personal_info_item:
                intent = new Intent(this, PersonalInfoDialog.class);
                rod = new RetirementOptionsData(mROM.getEndAge(), mROM.getWithdrawMode(), mROM.getWithdrawAmount(), mROM.getBirthdate(), mROM.getPercentIncrease());
                intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                overridePendingTransition(R.anim.slide_right_in, 0);
                break;
            case R.id.sign_out_item:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                FirebaseAuth  auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                mGoogleApiClient.disconnect();
                                Intent intent = new Intent(NavigationActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
            case R.id.revoke_item:
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                FirebaseAuth  auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                mGoogleApiClient.disconnect();
                                Intent intent = new Intent(NavigationActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mNeedToStartSummaryFragment) {
            // fragment transactions have to be handled outside of onActivityResult.
            // The state has already been saved and no state modifications are allowed.
            //startSummaryFragment();
            mNeedToStartSummaryFragment = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_RETIRE_OPTIONS:
                if (resultCode == RESULT_OK) {
                    RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
                    mROM = new RetirementOptionsEntity(mROM.getId(), rod.getEndAge(), rod.getWithdrawMode(), rod.getWithdrawAmount(), rod.getBirthdate(), rod.getPercentIncrease());
                    mViewModel.update(mROM.getId(), rod);
                }
                break;
            case REQUEST_PERSONAL_INFO:
                if (resultCode == RESULT_OK) {
                    String birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    mViewModel.updateBirthdate(birthdate);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void initBottomNavigation() {
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedNavFragment(item);
                return true;
            }
        });
    }

    private void selectedNavFragment(MenuItem item) {
        Fragment fragment;
        String fragmentTag;
        switch (item.getItemId()) {
            case R.id.home_menu:
                fragment = SummaryFragment.newInstance();
                fragmentTag = SUMMARY_FRAG_TAG;
                break;
            case R.id.income_menu:
                fragment = IncomeSourceListFragment.newInstance();
                fragmentTag = INCOME_FRAG_TAG;
                break;
            case R.id.milestones_menu:
                fragment = MilestoneAgesFragment.newInstance();
                fragmentTag = MILESTONES_FRAG_TAG;
                break;
            default:
                return;
        }
        mStartFragment = item.getItemId();



        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.content_frame);
        String oldTag = "";
        if(frag != null) {
            oldTag = frag.getTag();
        }
        Log.d(TAG, oldTag);
        if(oldTag.equals(fragmentTag)) {
            return;
        }
        FragmentTransaction ft;
        ft = fm.beginTransaction();
        handleAnimation(ft, oldTag, fragmentTag);
        ft.replace(R.id.content_frame, fragment, fragmentTag);
        ft.commit();
    }

    private void handleAnimation(FragmentTransaction ft, String oldTag, String newTag) {

        if (oldTag.isEmpty() || oldTag.equals(SUMMARY_FRAG_TAG)) {
            ft.setCustomAnimations(R.anim.slide_left_in,0);
        } else if (oldTag.equals(MILESTONES_FRAG_TAG)) {
            ft.setCustomAnimations(R.anim.slide_right_in, 0);
        } else if (oldTag.equals(INCOME_FRAG_TAG)) {
            if(newTag.equals(SUMMARY_FRAG_TAG)) {
                ft.setCustomAnimations(R.anim.slide_left_in, 0);
            } else {
                ft.setCustomAnimations(R.anim.slide_right_in, 0);
            }
        }
    }
}

