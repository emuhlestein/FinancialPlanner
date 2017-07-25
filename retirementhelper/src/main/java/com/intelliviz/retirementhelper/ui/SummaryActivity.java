package com.intelliviz.retirementhelper.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.income.IncomeSourceListFragment;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.ui.income.EditTaxDeferredIncomeFragment.TDID_STATUS_LOADER;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

/**
 * The summary activity.
 * @author Ed Muhlestein
 */
public class SummaryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = SummaryActivity.class.getSimpleName();
    private static final String SUMMARY_FRAG_TAG = "summary frag tag";
    private static final String INCOME_FRAG_TAG = "income frag tag";
    private static final String MILESTONES_FRAG_TAG = "milestones frag tag";
    private GoogleApiClient mGoogleApiClient;
    private boolean mNeedToStartSummaryFragment;
    private int mStartFragment;
    private TaxDeferredStatusAsyncHandler mTaxDeferredStatusAsyncHandler;

    @Bind(R.id.summary_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

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

        mNeedToStartSummaryFragment = true;

        mTaxDeferredStatusAsyncHandler = new TaxDeferredStatusAsyncHandler(getContentResolver());

        getSupportLoaderManager().initLoader(TDID_STATUS_LOADER, null, this);

        initBottomNavigation();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null) {
            MenuItem selectedItem;
            selectedItem = mBottomNavigation.getMenu().getItem(0);
            selectedNavFragment(selectedItem);
        }
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
        switch (item.getItemId()) {
            case R.id.retirement_options_item:
                intent = new Intent(this, RetirementOptionsDialog.class);
                RetirementOptionsData rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (rod != null) {
                    intent.putExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA, rod);
                    startActivityForResult(intent, REQUEST_RETIRE_OPTIONS);
                }
                break;
            case R.id.personal_info_item:
                intent = new Intent(this, PersonalInfoDialog.class);
                rod = RetirementOptionsHelper.getRetirementOptionsData(this);
                if (rod != null) {
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    startActivityForResult(intent, REQUEST_PERSONAL_INFO);
                }
                break;
            case R.id.sign_out_item:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
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
                            public void onResult(@NonNull Status status) {
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
    protected void onPause() {
        super.onPause();
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
                    SystemUtils.updateROD(this, rod);
                }
                break;
            case REQUEST_PERSONAL_INFO:
                if (resultCode == RESULT_OK) {
                    String birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    SystemUtils.updateBirthdate(this, birthdate);
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
        FragmentTransaction ft;
        ft = fm.beginTransaction();
        ft.replace(R.id.content_frame, fragment, fragmentTag);
        ft.commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case TDID_STATUS_LOADER:
                loader = new CursorLoader(this,
                        RetirementContract.TaxDeferredStatusEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case TDID_STATUS_LOADER:
                if(cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredStatusEntry.COLUMN_STATUS);
                    int resultIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredStatusEntry.COLUMN_RESULT);
                    int actionIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredStatusEntry.COLUMN_ACTION);
                    if(statusIndex != -1) {
                        int status = cursor.getInt(statusIndex);
                        if(status == RetirementContract.TaxDeferredStatusEntry.STATUS_UPDATED) {
                            if(actionIndex != -1 && resultIndex != -1) {
                                int action = cursor.getInt(actionIndex);
                                int numRows;
                                switch(action) {
                                    case RetirementContract.TaxDeferredStatusEntry.ACTION_DELETE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " deleted");
                                        break;
                                    case RetirementContract.TaxDeferredStatusEntry.ACTION_UPDATE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " update");
                                        break;
                                    case RetirementContract.TaxDeferredStatusEntry.ACTION_INSERT:
                                        String uri = cursor.getString(resultIndex);
                                        Log.d(TAG, uri + " inserted");
                                        break;
                                }
                            }
                            mTaxDeferredStatusAsyncHandler.clear();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public static class TaxDeferredStatusAsyncHandler extends AsyncQueryHandler {

        public TaxDeferredStatusAsyncHandler(ContentResolver cr) {
            super(cr);
        }

        public void clear() {
            Uri uri = RetirementContract.TaxDeferredStatusEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(RetirementContract.TaxDeferredStatusEntry.COLUMN_STATUS,
                    RetirementContract.TaxDeferredStatusEntry.STATUS_NONE);
            values.put(RetirementContract.TaxDeferredStatusEntry.COLUMN_ACTION,
                    RetirementContract.TaxDeferredStatusEntry.ACTION_NONE);
            values.put(RetirementContract.TaxDeferredStatusEntry.COLUMN_RESULT, "");
            startUpdate(0, null, uri, values, null, null);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if(result != 1) {
                Log.d(TAG, "Error updating status");
            }
        }
    }

}

