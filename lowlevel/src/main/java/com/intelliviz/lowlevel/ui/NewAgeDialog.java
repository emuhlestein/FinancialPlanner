package com.intelliviz.lowlevel.ui;

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
    private static final int MIN_MONTH = 0;
    private static final int MAX_MONTH = 11;
    private static final int MIN_YEAR = 0;
    private static final int MAX_YEAR = 100;
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private TextView mMessageTextView;

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

        String year = getArguments().getString(ARG_YEAR);
        String month = getArguments().getString(ARG_MONTH);

        TextView titleTextView = view.findViewById(R.id.title_view);
        mMessageTextView = view.findViewById(R.id.message_view);
        titleTextView.setText(getResources().getString(R.string.age));
        mMessageTextView.setText("");

        mYearEditText = view.findViewById(R.id.year_edit_text);
        mMonthEditText = view.findViewById(R.id.month_edit_text);
        mYearEditText.setText(year);
        mMonthEditText.setText(month);

        Button positiveButton = view.findViewById(R.id.positive_button);
        Button negativeButton = view.findViewById(R.id.negative_button);


        positiveButton.setText(getResources().getString(R.string.ok));
        negativeButton.setText(getResources().getString(R.string.cancel));


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAge()) {
                    sendResult();
                    dismiss();
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

    private boolean validateAge() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        try {
            if (Integer.parseInt(month) < MIN_MONTH || Integer.parseInt(month) > MAX_MONTH) {
                mMessageTextView.setText(getResources().getString(R.string.age_error_invalid_month));
                return false;
            }
        } catch(NumberFormatException e) {
            mMessageTextView.setText(getResources().getString(R.string.age_error_invalid_month));
            return false;
        }

        try {
            if (Integer.parseInt(year) < MIN_YEAR || Integer.parseInt(year) > MAX_YEAR) {
                mMessageTextView.setText(getResources().getString(R.string.age_error_invalid_year));
                return false;
            }
        } catch(NumberFormatException e) {
            mMessageTextView.setText(getResources().getString(R.string.age_error_invalid_year));
            return false;
        }

        return true;
    }

    private void sendResult() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();

        if(getTargetFragment() != null) {
            //Intent intent = new Intent();
            //intent.putExtra(EXTRA_DIALOG_RESPONSE, isOk);
            //getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            if(getActivity() instanceof NewAgeDialog.OnAgeEditListener) {
                NewAgeDialog.OnAgeEditListener listener = (NewAgeDialog.OnAgeEditListener) getActivity();
                listener.onEditAge(year, month);
            }
        }
    }
}
