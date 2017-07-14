package com.intelliviz.retirementhelper.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import butterknife.ButterKnife;

/**
 * Class to enter birthdates.
 * Created by Ed Muhlestein on 6/20/2017.
 */

public class BirthdateDialog extends AppCompatActivity {
    private static final String ARG_TITLE = "title";
    private EditText mBirthdateEditText;

    public BirthdateDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthdate_layout);
        ButterKnife.bind(this);

        /*
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.birthdate_layout, null);
        mBirthdateEditText = (EditText) view.findViewById(R.id.birthdate_edit_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.birthdate_format);
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

        return builder.create();
        */
    }
/*
    private void sendResult(int resultCode) {
        String birthdate = mBirthdateEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BIRTHDATE, birthdate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
    */
}
