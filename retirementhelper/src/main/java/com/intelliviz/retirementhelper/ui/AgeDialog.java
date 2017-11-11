package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;


public class AgeDialog extends DialogFragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private EditText mInputText;

    public static AgeDialog newInstance(String year, String month) {
        Bundle args = new Bundle();
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        AgeDialog fragment = new AgeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String year = getArguments().getString(ARG_YEAR);
        String inputText = getArguments().getString(ARG_MONTH);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.age_year_month_layout, null);

        setCancelable(false);
        //mInputText = new EditText(getContext());
        //mInputText.setEms(9);
        //mInputText.setText(inputText);
        //mInputText.setInputType(InputType.TYPE_CLASS_DATETIME);

        return new AlertDialog.Builder(getActivity())
                .setMessage("Add Age")
                .setView(view)
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
