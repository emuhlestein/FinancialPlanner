package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;

/**
 * Created by edm on 6/20/2017.
 */

public class BirthdateDialog extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private EditText mBirthdateEditText;

    public static BirthdateDialog newInstance(String title) {
        BirthdateDialog dialog = new BirthdateDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    public BirthdateDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.birthdate_layout, null);
        mBirthdateEditText = (EditText) view.findViewById(R.id.birthdate_edit_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Format (yyyy-MM-dd)");
        builder.setTitle(title);
        builder.setView(view);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendResult(Activity.RESULT_OK);
            }
        });

        setCancelable(false);

        Dialog dialog = builder.create();
        return dialog;
    }

    private void sendResult(int resultCode) {
        String birthdate = mBirthdateEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BIRTHDATE, birthdate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
