package com.intelliviz.retirementhelper.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.services.PersonalDataService;
import com.intelliviz.retirementhelper.util.GoogleApiClientHelper;
import com.intelliviz.retirementhelper.util.RetirementInfoMgr;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROD;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_LOGIN_RESPONSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PERSONAL_DATA;

public class StartActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = StartActivity.class.getSimpleName();
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";
    private static final int REQUEST_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private IdpResponse mResponse;
    @Bind(R.id.login_button)
    Button mLoginButton;

    @OnClick(R.id.login_button)
    public void login(View view) {
        mGoogleApiClient.connect();

        // need to see if user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startSignedInActivity(null);
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

    private BroadcastReceiver mPersonalInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
            PersonalInfoData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
            RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_ROD);
            RetirementInfoMgr.getInstance().setPersonalInfoData(pid);
            RetirementInfoMgr.getInstance().setRetirementInfoData(rod);
            Intent newIntent = new Intent(StartActivity.this, SummaryActivity.class);
            newIntent.putExtra(EXTRA_LOGIN_RESPONSE, mResponse);
            startActivity(newIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        GoogleApiClientHelper.createGoogleApiClient(this);
        mGoogleApiClient = GoogleApiClientHelper.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            mLoginButton.setText("Sign In");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                handleSignInResult(response);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void handleSignInResult(IdpResponse response) {
        if (response != null) {
            // Signed in successfully, show authenticated UI.
            mGoogleApiClient.connect();
            String email = response.getEmail();
            int err = response.getErrorCode();
            startSignedInActivity(null);
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
        List<String> result = new ArrayList<>();
        return result;
    }

    private void startSignedInActivity(IdpResponse response) {
        mResponse = response;
        mGoogleApiClient.connect();
        Intent intent = new Intent(this, PersonalDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_QUERY);
        startService(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect");
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_PERSONAL_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPersonalInfoReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPersonalInfoReceiver);
    }
}
