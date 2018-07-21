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
    private TextView mMessage;
    private TextView mTitle;
    private Button mOk;
    private Button mCancel;

    public interface DialogResponse {
        void onGetResponse(int response);
    }

    public static MessageDialog newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        MessageDialog fragment = new MessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        View view = inflater.inflate(R.layout.message_dialog_layout, null);

       mTitle = view.findViewById(R.id.title_view);
       mMessage = view.findViewById(R.id.message_view);
       mOk = view.findViewById(R.id.ok_button);
       mCancel = view.findViewById(R.id.cancel_button);

       mOk.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendResult(true);
           }
       });

       mCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                sendResult(false);
           }
       });

       mTitle.setText(title);
       mMessage.setText(message);

       return view;
    }

    private void sendResult(boolean isOk) {
        int resultCode = Activity.RESULT_OK;
        if(!isOk) {
            resultCode = Activity.RESULT_CANCELED;
        }

        if(getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
        } else {
            SimpleTextDialog.DialogResponse response = (SimpleTextDialog.DialogResponse) getActivity();
            response.onGetResponse(resultCode);
        }
    }
}
