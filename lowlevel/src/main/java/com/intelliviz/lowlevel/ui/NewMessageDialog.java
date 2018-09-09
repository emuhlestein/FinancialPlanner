package com.intelliviz.lowlevel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intelliviz.lowlevel.R;

public class NewMessageDialog extends DialogFragment {
    public static final int POS_BUTTON = 0;
    public static final int NEG_BUTTON = 1;
    public static final int NEU_BUTTON = 2;
    private static final int ONE_BUTTON = 1;
    private static final int TWO_BUTTON = 2;
    private static final int THREE_BUTTON = 3;
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ID = "id";
    private static final String ARG_POS_LABEL = "positive";
    private static final String ARG_NEG_LABEL = "negative";
    private static final String ARG_NEU_LABEL = "neutral";
    private static final String ARG_MODE = "mode";
    private TextView mMessage;
    private TextView mTitle;
    private int mId;

    public interface DialogResponse {
        void onGetResponse(int id, int button);
    }

    public static NewMessageDialog newInstance(int id, String title, String message, String posLabel) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_POS_LABEL, posLabel);
        args.putInt(ARG_MODE, ONE_BUTTON);
        args.putInt(ARG_ID, id);
        NewMessageDialog dialog = new NewMessageDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static NewMessageDialog newInstance(int id, String title, String message, String posLabel, String negLabel) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_POS_LABEL, posLabel);
        args.putString(ARG_NEG_LABEL, negLabel);
        args.putInt(ARG_MODE, TWO_BUTTON);
        args.putInt(ARG_ID, id);
        NewMessageDialog dialog = new NewMessageDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static NewMessageDialog newInstance(int id, String title, String message, String posLabel, String negLabel, String neuLabel) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_POS_LABEL, posLabel);
        args.putString(ARG_NEG_LABEL, negLabel);
        args.putString(ARG_NEU_LABEL, neuLabel);
        args.putInt(ARG_ID, id);
        NewMessageDialog dialog = new NewMessageDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_message_dialog_layout, container, false);

        mId = getArguments().getInt(ARG_ID);

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        mTitle = view.findViewById(R.id.title_view);
        mMessage = view.findViewById(R.id.message_view);
        mTitle.setText(title);
        mMessage.setText(message);

        int mode = getArguments().getInt(ARG_MODE);

        Button positiveButton = view.findViewById(R.id.positive_button);
        Button negativeButton = view.findViewById(R.id.negative_button);
        Button neutralButton = view.findViewById(R.id.neutral_button);

        String label = getArguments().getString(ARG_POS_LABEL);

        positiveButton.setText(label);
        if(mode == ONE_BUTTON) {
            negativeButton.setVisibility(View.GONE);
            neutralButton.setVisibility(View.GONE);
        }

        if(mode == TWO_BUTTON) {
            neutralButton.setVisibility(View.GONE);
            label = getArguments().getString(ARG_NEG_LABEL);
            negativeButton.setText(label);
        }

        if(mode == THREE_BUTTON) {
            neutralButton.setVisibility(View.GONE);
            label = getArguments().getString(ARG_NEU_LABEL);
            neutralButton.setText(label);
            label = getArguments().getString(ARG_NEG_LABEL);
            negativeButton.setText(label);
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(POS_BUTTON);
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(NEG_BUTTON);
            }
        });

        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(NEU_BUTTON);
            }
        });


        return view;
    }

    private void sendResult(int button) {
        if(getTargetFragment() != null) {
            Intent intent = new Intent();
            //intent.putExtra(EXTRA_DIALOG_RESPONSE, isOk);
            //getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            NewMessageDialog.DialogResponse response = (NewMessageDialog.DialogResponse) getActivity();
            response.onGetResponse(mId, button);
        }

        dismiss();
    }
}
