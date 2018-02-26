package com.intelliviz.retirementhelper.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.BirthdateDialogAction;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.StartUpViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCLUDE_SPOUSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_LOGIN_RESPONSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_SIGN_IN;

/**
 * The start activity
 * @author Ed Muhlestein
 */
public class StartActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, QueryCompleteListener {
    private static final String TAG = StartActivity.class.getSimpleName();
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";
    //private static final int REQUEST_SIGN_IN = 1;
    //private static final int REQUEST_BIRTHDATE = 2;
    private IdpResponse mResponse;
    private GoogleApiClient mGoogleApiClient;
    private StartUpViewModel mViewModel;
    private boolean mValidBirthdate = false;
    private RetirementOptionsEntity mROE;

    @BindView(R.id.login_button)
    Button mLoginButton;

    @OnClick(R.id.login_button)
    public void login(View view) {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        // need to see if user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            prepareToStartNavigateActivity(null);
            return;
        }
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(FIREBASE_TOS_URL)
                        .setPrivacyPolicyUrl(FIREBASE_PRIVACY_POLICY_URL)
                        .setIsSmartLockEnabled(false, false)
                        .setAllowNewEmailAccounts(true)
                        .build(),
                REQUEST_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        mGoogleApiClient = SystemUtils.createGoogleApiClient(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            mLoginButton.setText(R.string.sign_in);
        }

        mViewModel = ViewModelProviders.of(this).get(StartUpViewModel.class);

        mViewModel.get().observe(this, new Observer<RetirementOptionsEntity>() {
            @Override
            public void onChanged(@Nullable RetirementOptionsEntity roe) {
                mROE = roe;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            switch (requestCode) {
                case REQUEST_SIGN_IN:
                    IdpResponse response = IdpResponse.fromResultIntent(intent);
                    handleSignInResult(response);
                    break;
                case REQUEST_BIRTHDATE:
                    String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
                    String spouseBirthdate = intent.getStringExtra(EXTRA_SPOUSE_BIRTHDATE);
                    int includeSpouse = intent.getIntExtra(EXTRA_INCLUDE_SPOUSE, 0);
                    if(SystemUtils.validateBirthday(birthdate)) {
                        mViewModel.updateBirthdate(birthdate);
                        /*
                        BirthdateQueryHandler queryHandler = new BirthdateQueryHandler(getContentResolver(), this);
                        ContentValues values = new ContentValues();
                        values.put(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE, birthdate);
                        queryHandler.startUpdate(0, null, RetirementContract.RetirementParmsEntry.CONTENT_URI, values, null, null);
                        */
                    } else {
                        //Intent newIntent = new Intent(this, BirthdateActivity.class);
                        //startActivityForResult(newIntent, REQUEST_BIRTHDATE);
                        showDialog("01-01-1900", new BirthdateDialogAction() {
                            @Override
                            public void onGetBirthdate(String birthdate) {
                                mViewModel.updateBirthdate(birthdate);
                            }
                        });
                    }
                    break;
            }
        }
    }

    private void handleSignInResult(IdpResponse response) {
        if (response != null) {
            // Signed in successfully, show authenticated UI.
            if(mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
            prepareToStartNavigateActivity(response);
        }
    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());

        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        return selectedProviders;
    }

    private List<String> getGooglePermissions() {
        return new ArrayList<>();
    }

    private void prepareToStartNavigateActivity(IdpResponse response) {
        mResponse = response;

        boolean isBirthdateValid = false;
        if(SystemUtils.validateBirthday(mROE.getBirthdate())) {
            isBirthdateValid = true;
        }

        if(isBirthdateValid) {
            onStartNavigationActivity();
        } else {
            onStartBirthdateActivity(mROE.getBirthdate());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect");
    }

    @Override
    public void onStartNavigationActivity() {
        Intent newIntent = new Intent(this, NavigationActivity.class);
        newIntent.putExtra(EXTRA_LOGIN_RESPONSE, mResponse);
        startActivity(newIntent);
        finish();
    }

    @Override
    public void onStartBirthdateActivity(String birthdate) {
        //Intent newIntent = new Intent(this, BirthdateActivity.class);
        //newIntent.putExtra(EXTRA_BIRTHDATE, birthdate);
        //startActivityForResult(newIntent, REQUEST_BIRTHDATE);
        showDialog("01-01-1900", new BirthdateDialogAction() {
            @Override
            public void onGetBirthdate(String birthdate) {
                mViewModel.updateBirthdate(birthdate);
                onStartNavigationActivity();
            }
        });
    }

    private void showDialog(String birthdate, BirthdateDialogAction birthdateDialogAction) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BirthdateActivity birthdateDialog = BirthdateActivity.getInstance(birthdate, birthdateDialogAction);
        birthdateDialog.show(fm, "birhtdate");
    }
}
