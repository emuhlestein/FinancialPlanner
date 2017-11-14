package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;

/**
 * Activity for personal information.
 *
 * @author Ed Muhlestein
 */
public class PersonalInfoDialog extends AppCompatActivity {
    private RetirementOptionsData mROD;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    TextView mNameTextView;

    @BindView(R.id.email_edit_text)
    TextView mEmailTextView;

    @BindView(R.id.birthdate_edit_text)
    EditText mBirthDateEditText;

    @BindView(R.id.personal_info_ok)
    Button mOk;

    @BindView(R.id.personal_info_cancel)
    Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_dialog);
        ButterKnife.bind(this);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                overridePendingTransition(0, R.anim.slide_right_out);
            }
        });

        Intent intent = getIntent();
        mROD = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);

        updateUI();
    }

    @Override
    public void onBackPressed() {
        // disable back button; force user to use ok or cancel
    }

    private void updateUI() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = "";
        String displayName = "";
        if (user != null) {
            email = user.getEmail();
            if(email != null) {
                email = email.trim();
            }
            displayName = user.getDisplayName();
        }

        mNameTextView.setText(displayName);
        mBirthDateEditText.setText(mROD.getBirthdate());
        mEmailTextView.setText(email);

        if(!SystemUtils.validateBirthday(mROD.getBirthdate())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void sendData() {
        String birthday = mBirthDateEditText.getText().toString();
        if(!SystemUtils.validateBirthday(birthday)) {
            String message;
            String errMsg = getResources().getString(R.string.birthday_not_valid);
            message = errMsg + " " + DATE_FORMAT + ".";

            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_BIRTHDATE, birthday);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
