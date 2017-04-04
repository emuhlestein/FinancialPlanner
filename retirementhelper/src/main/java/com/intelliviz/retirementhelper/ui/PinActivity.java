package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.intelliviz.retirementhelper.R;

public class PinActivity extends AppCompatActivity {
    private static final int NUM_PIN_DIGITS = 4;
    private RadioButton[] mPinViews;
    private int mCurrentButton = 0;
    private char[] mPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

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
    }

    private void setPinDigit(char ch) {
        if(mCurrentButton == NUM_PIN_DIGITS) {
            return;
        }
        mPin[mCurrentButton] = ch;
        mPinViews[mCurrentButton].setChecked(true);
        mCurrentButton++;
    }
}
