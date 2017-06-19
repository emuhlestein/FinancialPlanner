package com.intelliviz.retirementhelper.util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by edm on 6/17/2017.
 */

public class GoogleApiClientHelper {
    private static final String TAG = GoogleApiClientHelper.class.getSimpleName();
    private static GoogleApiClient sGoogleApiClient = null;
    public static void createGoogleApiClient(Context context) {

        FragmentActivity fact;
        GoogleApiClient.OnConnectionFailedListener listener;
        try {
            fact = (FragmentActivity)context;
            listener = (GoogleApiClient.OnConnectionFailedListener)context;
        } catch(ClassCastException e) {
            Log.e(TAG, "Faile to create GoogleApiClient");
            return;
        }
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            sGoogleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage(fact, listener)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
    }

    public static GoogleApiClient getInstance() {
        return sGoogleApiClient;
    }

}
