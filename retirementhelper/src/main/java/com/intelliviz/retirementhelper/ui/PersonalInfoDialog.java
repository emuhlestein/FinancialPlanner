package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCLUDE_SPOUSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_SPOUSE_BIRTHDATE;

/**
 * Activity for personal information.
 *
 * @author Ed Muhlestein
 */
public class PersonalInfoDialog extends AppCompatActivity {
    private String mBirthdate;
    private String mSpouseBirthdate;
    private boolean mIncludeSpouse;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.name_edit_text)
    TextView mNameTextView;

    @BindView(R.id.email_edit_text)
    TextView mEmailTextView;

    @BindView(R.id.birthdate_text_view)
    TextView mBirthDateViewText;

    @BindView(R.id.spouse_birthdate_text_view)
    TextView mSpouseBirthDateViewText;

    @BindView(R.id.include_spouse_check_box)
    CheckBox mIncludeSpouseCheckBox;

    @BindView(R.id.birthdate_button)
    Button mBirthdateButton;

    @BindView(R.id.spouse_birthdate_button)
    Button mSpouseBirthdateButton;

    @OnClick(R.id.birthdate_button) void editBirthdate() {
        // TODO clean this up, maybe need default entity
        String birthdate;
        if(mBirthdate != null && !mBirthdate.isEmpty()) {
            birthdate = mBirthdate;
        } else {
            birthdate = "01-01-1900";
        }
        Intent newIntent = new Intent(this, BirthdateActivity.class);
        newIntent.putExtra(EXTRA_BIRTHDATE, birthdate);
        startActivityForResult(newIntent, REQUEST_BIRTHDATE);
    }

    @OnClick(R.id.spouse_birthdate_button) void editSpouseBirthdate() {
        // TODO clean this up, maybe need default entity
        String birthdate;
        if(mSpouseBirthdate != null && !mBirthdate.isEmpty()) {
            birthdate = mSpouseBirthdate;
        } else {
            birthdate = "01-01-1900";
        }
        Intent newIntent = new Intent(this, BirthdateActivity.class);
        newIntent.putExtra(EXTRA_BIRTHDATE, birthdate);
        startActivityForResult(newIntent, REQUEST_SPOUSE_BIRTHDATE);
    }

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
        mBirthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
        mSpouseBirthdate = intent.getStringExtra(EXTRA_SPOUSE_BIRTHDATE);
        int includeSpouse = intent.getIntExtra(EXTRA_INCLUDE_SPOUSE, 0);
        mIncludeSpouse = includeSpouse == 1 ? true : false;

        mIncludeSpouseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mSpouseBirthdateButton.setEnabled(true);
                    mSpouseBirthDateViewText.setEnabled(true);
                } else {
                    mSpouseBirthdateButton.setEnabled(false);
                    mSpouseBirthDateViewText.setEnabled(false);
                }
            }
        });

        if(mIncludeSpouse) {
            mSpouseBirthdateButton.setEnabled(true);
            mSpouseBirthDateViewText.setEnabled(true);
        } else {
            mSpouseBirthdateButton.setEnabled(false);
            mSpouseBirthDateViewText.setEnabled(false);
        }

        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        String birthdate;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BIRTHDATE:
                    birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    mBirthDateViewText.setText(birthdate);
                    break;
                case REQUEST_SPOUSE_BIRTHDATE:
                    birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    mSpouseBirthDateViewText.setText(birthdate);
                    mSpouseBirthdate = birthdate;
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, intent);
            }
        }
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
        mBirthDateViewText.setText(mBirthdate);
        mEmailTextView.setText(email);
        mSpouseBirthDateViewText.setText(mSpouseBirthdate);
        mIncludeSpouseCheckBox.setChecked(mIncludeSpouse);

        if(!SystemUtils.validateBirthday(mBirthdate)) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.enter_birthdate), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void sendData() {
        String birthday = mBirthDateViewText.getText().toString();
        if(!SystemUtils.validateBirthday(birthday)) {
            String message;
            String errMsg = getResources().getString(R.string.birthday_not_valid);
            message = errMsg + " " + DATE_FORMAT + ".";
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String spouseBirthday = mSpouseBirthDateViewText.getText().toString();
        if(!SystemUtils.validateBirthday(spouseBirthday)) {
            String message;
            String errMsg = getResources().getString(R.string.birthday_not_valid);
            message = errMsg + " " + DATE_FORMAT + ".";
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        int includeSpouse = mIncludeSpouseCheckBox.isChecked() ? 1 : 0;

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RetirementConstants.EXTRA_BIRTHDATE, birthday);
        returnIntent.putExtra(RetirementConstants.EXTRA_SPOUSE_BIRTHDATE, spouseBirthday);
        returnIntent.putExtra(RetirementConstants.EXTRA_INCLUDE_SPOUSE, includeSpouse);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
