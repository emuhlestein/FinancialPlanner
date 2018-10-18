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

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_DIALOG_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;


/**
 * Dialog for gathering simple text.
 * @author Ed Muhlestein
 */
public class SimpleTextDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_INPUT = "input";
    private static final String ARG_ID = "id";
    private EditText mInputText;
    private int mId;

    public interface DialogResponse {
        void onGetResponse(int id, boolean isOk, String message);
    }

    public static SimpleTextDialog newInstance(int id, String message, String inputText) {
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
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
        mId = getArguments().getInt(ARG_ID);

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

        if(getTargetFragment() != null) {
            int resultCode = Activity.RESULT_OK;
            if(!isOk) {
                resultCode = Activity.RESULT_CANCELED;
            }
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DIALOG_INPUT_TEXT, enteredText);
            intent.putExtra(EXTRA_DIALOG_ID, mId);

            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            DialogResponse response = (DialogResponse) getActivity();
            response.onGetResponse(mId, isOk, enteredText);
        }
    }
}
