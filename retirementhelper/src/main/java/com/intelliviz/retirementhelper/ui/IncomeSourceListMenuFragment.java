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
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeSourceListMenuFragment extends DialogFragment {
    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;
    private static final String ARG_ID = "arg id";
    private static final String ARG_TYPE = "arg type";
    private long mIncomeSourceId;
    private int mIncomeSourceType;

    public static IncomeSourceListMenuFragment newInstance(long id, int type) {
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putInt(ARG_TYPE, type);
        IncomeSourceListMenuFragment fragment = new IncomeSourceListMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIncomeSourceType = getArguments().getInt(ARG_TYPE);
        mIncomeSourceId = getArguments().getLong(ARG_ID);

        final String[] incomeActions = getResources().getStringArray(R.array.income_source_actions);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(incomeActions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                Toast.makeText(getContext(), "You selected " + incomeActions[item], Toast.LENGTH_LONG).show();
                dialogInterface.dismiss();
                sendResult(Activity.RESULT_OK, item);
                /*
                Intent intent = new Intent(getContext(), IncomeSourceActivity.class);

                switch(mIncomeSourceType) {
                    case RetirementConstants.INCOME_TYPE_SAVINGS:
                        if(item == MENU_EDIT) {
                            SavingsIncomeData sid = DataBaseUtils.getSavingsIncomeData(getContext(), mIncomeSourceId);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
                            intent.putExtra(EXTRA_INCOME_DATA, sid);
                            intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_EDIT);
                            startActivityForResult(intent, REQUEST_SAVINGS);
                        } else if(item == MENU_DELETE) {
                            int rowsDeleted = DataBaseUtils.deleteSavingsIncome(getContext(), mIncomeSourceId);
                        }
                        break;
                    case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                        if(item == MENU_EDIT) {
                            TaxDeferredIncomeData tdid = DataBaseUtils.getTaxDeferredIncomeData(getContext(), mIncomeSourceId);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
                            intent.putExtra(EXTRA_INCOME_DATA, tdid);
                            intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);
                            intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_EDIT);
                            startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                        } else if(item == MENU_DELETE) {
                            int rowsDeleted = DataBaseUtils.deleteTaxDeferredIncome(getContext(), mIncomeSourceId);
                        }
                        break;
                }
                */
            }
        });
        return builder.create();
    }

    private void sendResult(int resultCode, int menuItem) {
        Intent intent = new Intent();

        int action = -1;
        if(menuItem == MENU_EDIT) {
            action = INCOME_ACTION_EDIT;
        } else if(menuItem == MENU_DELETE) {
            action = INCOME_ACTION_DELETE;
        }
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
