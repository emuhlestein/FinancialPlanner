package com.intelliviz.retirementhelper.ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;

/**
 * A simple {@link Fragment} subclass.
 */
public class YesNoDialog extends DialogFragment {
    private static final String ARG_ID = "arg id";
    private long mIncomeSourceId;

    public static YesNoDialog newInstance(long id) {
        YesNoDialog dialog = new YesNoDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        dialog.setArguments(args);
        return dialog;
    }

    public YesNoDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIncomeSourceId = getArguments().getLong(ARG_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Delete income source?");
        builder.setTitle("Alert");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendResult(Activity.RESULT_OK);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    private void sendResult(int resultCode) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_DELETE);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
