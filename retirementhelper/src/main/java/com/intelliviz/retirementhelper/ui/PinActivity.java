package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PinActivity extends AppCompatActivity implements UserInfoQueryListener {
    public static final String START_REASON = "reason";
    public static final int NEW_PIN = 1;
    public static final int SIGN_IN_PIN = 2;
    private static final int NUM_PIN_DIGITS = 4;
    private RadioButton[] mPinViews;
    private int mCurrentButton = 0;
    private char[] mPin;
    private String mPin1;
    private String mPin2;
    private boolean mFirstPin = true;
    private int mStartReason;
    @Bind(R.id.pin_label) TextView mPinLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);

        mPin = new char[NUM_PIN_DIGITS];
        mPin[0] = ' ';
        mPin[1] = ' ';
        mPin[2] = ' ';
        mPin[3] = ' ';

        mPinViews = new RadioButton[NUM_PIN_DIGITS];
        mPinViews[0] = (RadioButton) findViewById(R.id.pin_1);
        mPinViews[1] = (RadioButton) findViewById(R.id.pin_2);
        mPinViews[2] = (RadioButton) findViewById(R.id.pin_3);
        mPinViews[3] = (RadioButton) findViewById(R.id.pin_4);

        Intent intent = getIntent();
        mStartReason = intent.getIntExtra(START_REASON, NEW_PIN);
        mPinLabel.setText("Please re-enter pin");
    }

    public void onClickPinButton(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.pin_0_button:
                setPinDigit('0');
                break;
            case R.id.pin_1_button:
                setPinDigit('1');
                break;
            case R.id.pin_2_button:
                setPinDigit('2');
                break;
            case R.id.pin_3_button:
                setPinDigit('3');
                break;
            case R.id.pin_4_button:
                setPinDigit('4');
                break;
            case R.id.pin_5_button:
                setPinDigit('5');
                break;
            case R.id.pin_6_button:
                setPinDigit('6');
                break;
            case R.id.pin_7_button:
                setPinDigit('7');
                break;
            case R.id.pin_8_button:
                setPinDigit('8');
                break;
            case R.id.pin_9_button:
                setPinDigit('9');
                break;
            case R.id.pin_x_button:
                mCurrentButton--;
                if(mCurrentButton < 0) {
                    mCurrentButton = 0;
                }
                mPinViews[mCurrentButton].setChecked(false);
                break;
            default:
                return;
        }

        if(mCurrentButton == NUM_PIN_DIGITS) {
            String pin = String.valueOf(mPin);
            Toast.makeText(this, pin, Toast.LENGTH_LONG).show();

            if(mStartReason == NEW_PIN) {
                createNewPIN(pin);
            } else {
                // sign in with pin
                signInWithPIN(pin);
            }
        }
    }

    private void createNewPIN(String pin) {
        if(mFirstPin) {
            // need to have pin re-entered to validate.
            mFirstPin = false;
            mPin1 = pin;
            mPinViews[0].setChecked(false);
            mPinViews[1].setChecked(false);
            mPinViews[2].setChecked(false);
            mPinViews[3].setChecked(false);
            mPinLabel.setText("Please re-enter pin");
        } else {
            if(pin.equals(mPin1)) {
                // pins match.
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", pin);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(this, "Pins entered pins do not match. Please try again.", Toast.LENGTH_SHORT).show();
                mFirstPin = true;
            }
        }
    }

    private void signInWithPIN(String pin) {

    }

    private void setPinDigit(char ch) {
        if(mCurrentButton == NUM_PIN_DIGITS) {
            return;
        }
        mPin[mCurrentButton] = ch;
        mPinViews[mCurrentButton].setChecked(true);
        mCurrentButton++;
    }

    @Override
    public void onQueryUserInfo(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onInsertUserInfo(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateUserInfo(int token, Object cookie, int result) {

    }
}
