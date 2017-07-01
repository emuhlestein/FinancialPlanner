package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_MESSAGE;

public class SimpleTextDialog extends AppCompatActivity {

    @Bind(R.id.message_text_view)
    TextView mMessageTextView;

    @Bind(R.id.message_edit_text)
    EditText mMessageEditText;

    @OnClick(R.id.cancel_button) void onCancelClick() {
        finish();
    }

    @OnClick(R.id.yes_button) void onYesClick() {
        sendResult();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_text);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_DIALOG_MESSAGE);
        mMessageTextView.setText(message);
    }

    private void sendResult() {
        String enteredText = mMessageEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DIALOG_INPUT_TEXT, enteredText);
        setResult(Activity.RESULT_OK, intent);
    }
}
