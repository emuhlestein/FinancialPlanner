package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_LOGIN_RESPONSE;

/**
 * The start activity
 * @author Ed Muhlestein
 */
public class StartActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = StartActivity.class.getSimpleName();
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";
    private static final int REQUEST_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.login_button)
    Button mLoginButton;

    @OnClick(R.id.login_button)
    public void login(View view) {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

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

    private void handleSignInResult(IdpResponse response) {
        if (response != null) {
            // Signed in successfully, show authenticated UI.
            if(mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
            startSignedInActivity(response);
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

    private void startSignedInActivity(IdpResponse response) {
        Intent newIntent = new Intent(this, BirthdateActivity.class);
        newIntent.putExtra(EXTRA_LOGIN_RESPONSE, response);
        startActivity(newIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect");
    }
}
