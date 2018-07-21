package com.intelliviz.lowlevel.ui;

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

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;


/**
 * Dialog for gathering simple text.
 * @author Ed Muhlestein
 */
public class SimpleTextDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_INPUT = "input";
    private EditText mInputText;

    public interface DialogResponse {
        void onGetResponse(int response);
    }

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

        if(inputText != null) {
            mInputText = new EditText(getContext());
            mInputText.setText(inputText);
            mInputText.setSingleLine(false);
            mInputText.setInputType(InputType.TYPE_CLASS_DATETIME);
        } else {
            mInputText = null;
        }

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setView(mInputText)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendResult(true);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendResult(false);
                            }
                        })
                .create();
    }

    private void sendResult(boolean isOk) {
        String enteredText = mInputText.getText().toString();
        int resultCode = Activity.RESULT_OK;
        if(!isOk) {
            resultCode = Activity.RESULT_CANCELED;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DIALOG_INPUT_TEXT, enteredText);

        if(getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            DialogResponse response = (DialogResponse) getActivity();
            response.onGetResponse(resultCode);
        }
    }
}
