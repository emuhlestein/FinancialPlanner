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
import android.widget.TextView;

import com.intelliviz.lowlevel.R;

public class MessageDialog extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ID = "id";
    private static final String ARG_OK_ONLY = "ok";
    private static final String ARG_NO_LABEL = "no";
    private static final String ARG_YES_LABEL = "yes";
    private TextView mMessage;
    private TextView mTitle;
    private Button mOk;
    private Button mCancel;
    private int mId;

    public interface DialogResponse {
        void onGetResponse(int response, int id, boolean isOk);
    }

    public static MessageDialog newInstance(String title, String message, int id, boolean okOnly, String noLabel, String yesLabel) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ID, id);
        args.putBoolean(ARG_OK_ONLY, okOnly);
        args.putString(ARG_NO_LABEL, noLabel);
        args.putString(ARG_YES_LABEL, yesLabel);
        MessageDialog fragment = new MessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        final boolean okOnly = getArguments().getBoolean(ARG_OK_ONLY);
        mId = getArguments().getInt(ARG_ID);
        final String noLabel = getArguments().getString(ARG_NO_LABEL);
        final String yesLabel = getArguments().getString(ARG_YES_LABEL);

        View view;
        if(okOnly) {
            view = inflater.inflate(R.layout.message_dialog_one_button_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.message_dialog_layout, container, false);
            mCancel = view.findViewById(R.id.cancel_button);
            if(noLabel != null) {
                mCancel.setText(noLabel);
            }
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(okOnly) {
                        sendResult(false, Activity.RESULT_CANCELED);
                    } else {
                        sendResult(false, Activity.RESULT_OK);
                    }
                }
            });
        }

       mTitle = view.findViewById(R.id.title_view);
       mMessage = view.findViewById(R.id.message_view);
       mOk = view.findViewById(R.id.ok_button);

       if(yesLabel != null) {
            mOk.setText(yesLabel);
       }


       mOk.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendResult(true, Activity.RESULT_OK);
           }
       });

       mTitle.setText(title);
       mMessage.setText(message);

       return view;
    }

    private void sendResult(boolean isOk, int resultCode) {
        if(getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
        } else {
            MessageDialog.DialogResponse response = (MessageDialog.DialogResponse) getActivity();
            response.onGetResponse(resultCode, mId, isOk);
        }

        dismiss();
    }
}
