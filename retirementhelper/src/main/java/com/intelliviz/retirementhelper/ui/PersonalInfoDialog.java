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

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;

public class PersonalInfoDialog extends AppCompatActivity {
    private RetirementOptionsData mROD;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    TextView mNameTextView;

    @Bind(R.id.email_edit_text)
    TextView mEmailTextView;

    @Bind(R.id.birthdate_edit_text)
    EditText mBirthDateEditText;

    @Bind(R.id.personal_info_ok)
    Button mOk;

    @Bind(R.id.personal_info_cancel)
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
            displayName = user.getDisplayName();
        }
        Intent intent = getIntent();

        mNameTextView.setText(displayName);
        mBirthDateEditText.setText(mROD.getBirthdate());
        mEmailTextView.setText(email);

        if(!SystemUtils.validateBirthday(mROD.getBirthdate())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Please enter your birthdate", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void sendData() {
        String name = mNameTextView.getText().toString();
        String birthday = mBirthDateEditText.getText().toString();
        if(!SystemUtils.validateBirthday(birthday)) {
            String errMsg = getResources().getString(R.string.birthday_not_valid);
            String yearFormat = DATE_FORMAT;
            StringBuilder sb = new StringBuilder();
            sb.append(errMsg);
            sb.append(" ");
            sb.append(yearFormat);
            sb.append(".");

            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, sb.toString(), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_BIRTHDATE, birthday);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
