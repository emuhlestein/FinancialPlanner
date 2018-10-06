package com.intelliviz.lowlevel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.lowlevel.R;

public class NewAgeDialog extends DialogFragment {
    private static final String ARG_ID = "arg_id";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private TextView mMessageTextView;
    private int mId;

    public interface OnAgeEditListener {
        void onEditAge(String year, String month);
    }

    public static NewAgeDialog newInstance(String year, String month) {
        Bundle args = new Bundle();
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        NewAgeDialog dialog = new NewAgeDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_age_dialog_layout, container, false);

        mId = getArguments().getInt(ARG_ID);
        String year = getArguments().getString(ARG_YEAR);
        String month = getArguments().getString(ARG_MONTH);

        TextView titleTextView = view.findViewById(R.id.title_view);
        mMessageTextView = view.findViewById(R.id.message_view);
        titleTextView.setText("Age");
        mMessageTextView.setText("");

        mYearEditText = view.findViewById(R.id.year_edit_text);
        mMonthEditText = view.findViewById(R.id.month_edit_text);
        mYearEditText.setText(year);
        mMonthEditText.setText(month);

        Button positiveButton = view.findViewById(R.id.positive_button);
        Button negativeButton = view.findViewById(R.id.negative_button);


        positiveButton.setText("Ok");
        negativeButton.setText("Cancel");


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAgeValid()) {
                    sendResult();
                } else {
                    mMessageTextView.setText("Error: ");
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        return view;
    }

    private boolean isAgeValid() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        if(Integer.parseInt(month) < 0 || Integer.parseInt(month) > 11) {

            return false;
        }

        return true;
    }

    private void sendResult() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        if(getTargetFragment() != null) {
            Intent intent = new Intent();
            //intent.putExtra(EXTRA_DIALOG_RESPONSE, isOk);
            //getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            if(getActivity() instanceof NewAgeDialog.OnAgeEditListener) {
                NewAgeDialog.OnAgeEditListener listener = (NewAgeDialog.OnAgeEditListener) getActivity();
                listener.onEditAge(year, month);
            }
        }

        dismiss();
    }
}
