package com.intelliviz.retirementhelper.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * Class to manager the registration of a new user.
 * CLass gathers user name (email) and password information.
 * @author Ed Muhlestein
 */
public class NewUserActivity extends AppCompatActivity implements OnLoadPersonalInfoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        ActionBar ab = getSupportActionBar();
        ab.setSubtitle("New User");
    }

    public void registerUser(View view) {
        String email = "";
        validateEmail(email);
        validatePassword();
        Intent intent = new Intent(this, SummaryActivity.class);
        startActivity(intent);
    }

    private void validatePassword() {

    }

    private void validateEmail(String email) {
        EmailQueryHandler emailQueryHandler =
                new EmailQueryHandler(getContentResolver(), this, email);
        String selection = RetirementContract.PeronsalInfoEntry.TABLE_NAME + "." +
                RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Uri uri = RetirementContract.PeronsalInfoEntry.CONTENT_URI.buildUpon().appendPath(email).build();
        emailQueryHandler.startQuery(1, null, uri, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadEmail(Cursor cursor, String email) {
        if(cursor == null || !cursor.moveToFirst()) {
            return;
        }

        String dbEmail = "";
        int emailIndex = cursor.getColumnIndex(RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL);
        if(emailIndex != -1) {
            dbEmail = cursor.getString(emailIndex);
            if(dbEmail.equals(email)) {
                // email already in use
            }
        }
    }

    private class EmailQueryHandler extends AsyncQueryHandler {

        private WeakReference<OnLoadPersonalInfoListener> mListener;
        private String mEmail;

        public EmailQueryHandler(ContentResolver cr, OnLoadPersonalInfoListener listener, String email) {
            super(cr);
            mListener = new WeakReference<>(listener);
            mEmail = email;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final OnLoadPersonalInfoListener listener = mListener.get();
            if(listener != null) {
                listener.onLoadEmail(cursor, mEmail);
            }
        }
    }
}
