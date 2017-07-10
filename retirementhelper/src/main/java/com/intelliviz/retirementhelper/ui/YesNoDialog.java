package com.intelliviz.retirementhelper.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_MESSAGE;

/**
 * Yes/No dialog;
 * @author Ed Muhlestein
 */
public class YesNoDialog extends AppCompatActivity {
    @Bind(R.id.yes_no_message_text)
    TextView mYesNoMessageText;

    @OnClick(R.id.yes_button) void onYesClick() {
        sendResult();
        finish();
    }

    @OnClick(R.id.no_button) void onNoClick() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_no_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_DIALOG_MESSAGE);
        mYesNoMessageText.setText(message);
    }

    private void sendResult() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
    }
}
