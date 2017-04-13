package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.db.RetirementContract;

public class MainActivity extends AppCompatActivity {
    private final static int PIN_REQUEST = 1;
    private String mPin = "-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = RetirementContract.PeronsalInfoEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL);
            int pinIndex = cursor.getColumnIndex(RetirementContract.PeronsalInfoEntry.COLUMN_PIN);
            String email = cursor.getString(emailIndex);
            mPin = cursor.getString(pinIndex);
            if(!email.equals("-1")) {
                Intent intent = new Intent(this, PinActivity.class);
                intent.putExtra(PinActivity.START_REASON, PinActivity.SIGN_IN_PIN);
                startActivityForResult(intent, PIN_REQUEST);
            } else {
                setContentView(R.layout.activity_main);
                Button mNewUserButton = (Button) findViewById(R.id.new_user_button);
                mNewUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                        startActivity(intent);
                    }
                });
            }
        } else {
            setContentView(R.layout.activity_main);
            Button mNewUserButton = (Button) findViewById(R.id.new_user_button);
            mNewUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String pin = bundle.getString("result");
                if(pin.equals(mPin)) {
                    Intent intent = new Intent(this, SummaryActivity.class);
                    startActivity(intent);
                } else {
                    // relaunch pin activity
                }
            }
        }
    }
}
