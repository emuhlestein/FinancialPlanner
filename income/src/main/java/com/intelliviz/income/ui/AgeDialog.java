package com.intelliviz.income.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.income.R;


public class AgeDialog extends DialogFragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private TextView mMessage;
    private OnAgeEditListener mListener;

    public interface OnAgeEditListener {
        void onEditAge(String year, String month);
    }

    public static AgeDialog newInstance(String year, String month) {
        Bundle args = new Bundle();
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        AgeDialog fragment = new AgeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String year = getArguments().getString(ARG_YEAR);
        String month = getArguments().getString(ARG_MONTH);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.age_year_month_layout, null);

        mYearEditText = view.findViewById(R.id.year_edit_text);
        mMonthEditText = view.findViewById(R.id.month_edit_text);
        mMessage = view.findViewById(R.id.error_message);

        mYearEditText.setText(year);
        mMonthEditText.setText(month);

        setCancelable(false);

        return new AlertDialog.Builder(getActivity())
                .setMessage("Add Age")
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!isAgeValid()) {
                                    return;
                                }
                                //dialog.dismiss();
                                sendResult();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.dismiss();
                            }
                        })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAgeEditListener) {
            mListener = (OnAgeEditListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean isAgeValid() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        mMessage.setText("Error: ");
        if(Integer.parseInt(month) < 0 || Integer.parseInt(month) > 11) {
            return false;
        }

        return true;
    }


    private void sendResult() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        mMessage.setText("Error: ");
        if(Integer.parseInt(month) < 0 || Integer.parseInt(month) > 11) {
            return;
        }

        if (getTargetFragment() != null) {
            mListener = (OnAgeEditListener) getTargetFragment();
            mListener.onEditAge(year, month);
        } else if(mListener != null) {
            mListener.onEditAge(year, month);
        }
    }
}
