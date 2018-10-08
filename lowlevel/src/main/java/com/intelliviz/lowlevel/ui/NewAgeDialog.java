package com.intelliviz.lowlevel.ui;

import android.app.Activity;
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

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_AGE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MONTH;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_YEAR;

public class NewAgeDialog extends DialogFragment {
    private static final int MIN_MONTH = 0;
    private static final int MAX_MONTH = 11;
    private static final int MIN_YEAR = 0;
    private static final int MAX_YEAR = 100;
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private static final String ARG_TITLE = "title";
    private static final String ARG_AGE_ID = "id";
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private TextView mMessageTextView;
    private int mAgeId;

    public interface OnAgeEditListener {
        void onEditAge(int id, String year, String month);
    }

    public static NewAgeDialog newInstance(int ageId, String year, String month) {
        return createIstance(ageId, year, month, null);
    }

    public static NewAgeDialog newInstance(int ageId, String year, String month, String title) {
        return createIstance(ageId, year, month, title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_age_dialog_layout, container, false);

        mAgeId = getArguments().getInt(ARG_AGE_ID);
        String year = getArguments().getString(ARG_YEAR);
        String month = getArguments().getString(ARG_MONTH);
        String title = getArguments().getString(ARG_TITLE);
        if(title == null) {
            title = getResources().getString(R.string.age);
        }

        TextView titleTextView = view.findViewById(R.id.title_view);
        mMessageTextView = view.findViewById(R.id.message_view);
        titleTextView.setText(title);
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
            Intent intent = new Intent();
            intent.putExtra(EXTRA_AGE_ID, mAgeId);
            intent.putExtra(EXTRA_MONTH, month);
            intent.putExtra(EXTRA_YEAR, year);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        } else {
            if(getActivity() instanceof NewAgeDialog.OnAgeEditListener) {
                NewAgeDialog.OnAgeEditListener listener = (NewAgeDialog.OnAgeEditListener) getActivity();
                listener.onEditAge(mAgeId, year, month);
            }
        }
    }

    private static NewAgeDialog createIstance(int ageId, String year, String month, String title) {
        Bundle args = new Bundle();
        args.putInt(ARG_AGE_ID, ageId);
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        if(title != null) {
            args.putString(ARG_TITLE, title);
        }
        NewAgeDialog dialog = new NewAgeDialog();
        dialog.setArguments(args);
        return dialog;
    }
}
