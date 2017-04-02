package com.intelliviz.retirementhelper.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class to manager the registration of a new user.
 * CLass gathers user name (email) and password information.
 * @author Ed Muhlestein
 */
public class NewUserActivity extends AppCompatActivity implements UserInfoQueryListener {
    private String mEmail;
    private String mPassword;
    @Bind(R.id.password_edit_text) EditText mPasswordEditText;
    @Bind(R.id.password2_edit_text) EditText mPassword2EditText;
    @Bind(R.id.email_edit_text) EditText mEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        // TODO create toolbar
        //ActionBar ab = getSupportActionBar();
        //ab.setSubtitle("New User");
    }

    public void registerUser(View view) {

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String password2 = mPassword2EditText.getText().toString();
        if(!validateEmail(email)) {
            // TODO pop up toast saying that email is invalid
            return;
        }

        if(!validatePassword(password, password2)) {
            // TODO pop up toast saying password is not valid
            return;
        }

        mEmail = email;
        mPassword = password;

        // now check password in db.
        validateUser();


    }

    /**
     * Passwords must match and must be valid.
     * TODO valid password rules need to be defined.
     */
    private boolean validatePassword(String password1, String password2) {
        return true;
    }

    /**
     * Passwords must match and must be valid.
     * TODO valid password rules need to be defined.
     */
    private boolean validateEmail(String email) {
        return true;
    }

    private void validateUser() {
        UserInfoQueryHandler userInfoQueryHandler =
                new UserInfoQueryHandler(getContentResolver(), this);
        String selection = RetirementContract.PeronsalInfoEntry.TABLE_NAME + "." +
                RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {mEmail};

        Uri uri = RetirementContract.PeronsalInfoEntry.CONTENT_URI.buildUpon().appendPath(mEmail).build();
        userInfoQueryHandler.startQuery(1, null, uri, null, selection, selectionArgs, null);
    }

    @Override
    public void onQueryUserInfo(int token, Object cookie, Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            // email is valid and does not exist in db; can add it
            addUserInfo();
        } else {
            // email already exists; don't add it.
            // TODO pop up toast that says email already in use
        }
    }

    @Override
    public void onInsertUserInfo(int token, Object cookie, Uri uri) {
        String id = uri.getLastPathSegment();
        if(!id.equals(-1)) {
            Toast.makeText(this, "Successfully add " + mEmail, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SummaryActivity.class);
            startActivity(intent);
        }
    }

    private void addUserInfo() {
        UserInfoQueryHandler userInfoQueryHandler =
                new UserInfoQueryHandler(getContentResolver(), this);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL, mEmail);
        values.put(RetirementContract.PeronsalInfoEntry.COLUMN_PASSWORD, mPassword);
        values.put(RetirementContract.PeronsalInfoEntry.COLUMN_BIRTHDATE, "");
        values.put(RetirementContract.PeronsalInfoEntry.COLUMN_NAME, "");
        //Uri uri = RetirementContract.PeronsalInfoEntry.CONTENT_URI.buildUpon().appendPath(mEmail).build();
        userInfoQueryHandler.startInsert(1, null, RetirementContract.PeronsalInfoEntry.CONTENT_URI, values);
    }

    private class UserData {
        public String email;
        public String password;
        public UserData(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }


    private class UserInfoQueryHandler extends AsyncQueryHandler {

        private WeakReference<UserInfoQueryListener> mListener;

        public UserInfoQueryHandler(ContentResolver cr, UserInfoQueryListener listener) {
            super(cr);
            mListener = new WeakReference<>(listener);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final UserInfoQueryListener listener = mListener.get();
            if(listener != null) {
                listener.onQueryUserInfo(token, cookie, cursor);
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            final UserInfoQueryListener listener = mListener.get();
            if(listener != null) {
                listener.onInsertUserInfo(token, cookie, uri);
            }
        }
    }
}
