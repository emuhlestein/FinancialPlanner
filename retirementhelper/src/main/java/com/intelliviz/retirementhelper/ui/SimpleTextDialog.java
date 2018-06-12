package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import static com.intelliviz.income.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;


/**
 * Dialog for gathering simple text.
 * @author Ed Muhlestein
 */
public class SimpleTextDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_INPUT = "input";
    private EditText mInputText;

    public static SimpleTextDialog newInstance(String message, String inputText) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_INPUT, inputText);
        SimpleTextDialog fragment = new SimpleTextDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ARG_MESSAGE);
        String inputText = getArguments().getString(ARG_INPUT);

        setCancelable(false);
        mInputText = new EditText(getContext());
        mInputText.setEms(9);
        mInputText.setText(inputText);
        mInputText.setInputType(InputType.TYPE_CLASS_DATETIME);

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setView(mInputText)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendResult();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
    }

    private void sendResult() {
        if(getTargetFragment() == null) {
            return;
        }
        String enteredText = mInputText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DIALOG_INPUT_TEXT, enteredText);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
