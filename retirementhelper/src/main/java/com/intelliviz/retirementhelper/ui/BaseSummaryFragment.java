package com.intelliviz.retirementhelper.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.SummaryMilestoneAdapter;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.db.RetirementOptionsDatabase;
import com.intelliviz.retirementhelper.services.RetirementOptionsService;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_INPUT_TEXT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.TRANS_TYPE_MILESTONE_SUMMARY;

/**
 * @author Ed Muhlestein
 * Created on 8/7/2017.
 */

public abstract class BaseSummaryFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SummaryMilestoneAdapter.SelectionMilestoneListener{
    private static final String DIALOG_INPUT_TEXT = "DialogInputText";
    private static final int STATUS_LOADER = 0;
    private static final int MILESTONE_SUMMARY_LOADER = 1;
    private SummaryMilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalanceTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Intent intent = new Intent(getContext(), RetirementOptionsService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_QUERY);
        getContext().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);

        createBannerAdd();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.summary_screen_subtitle));
        }

        getLoaderManager().initLoader(STATUS_LOADER, null, this);

        mMilestoneAdapter = new SummaryMilestoneAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        List<IncomeType> incomeSources = DataBaseUtils.getAllIncomeTypes(getContext());
        if(incomeSources.isEmpty()) {
            final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.add_income_source_message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

        // TODO see if birthday has been set. if not force user to set it.
        SystemUtils.updateAppWidget(getContext());
        DataBaseUtils.updateMilestoneData(getContext());

        return view;
    }

    public void createBannerAdd() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BIRTHDATE:
                    onHandleBirthdate(intent);
                    break;
            }
        }
    }

    private void updateUI(double currentBalance) {
        String formattedAmount = SystemUtils.getFormattedCurrency(currentBalance);
        mCurrentBalanceTextView.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestone(Cursor cursor) {
        MilestoneData msd = DataBaseUtils.extractData(cursor);
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }

    private void onHandleBirthdate(Intent intent) {
        String birthdate = intent.getStringExtra(EXTRA_DIALOG_INPUT_TEXT);
        if (!SystemUtils.validateBirthday(birthdate)) {
            String message = String.format(getString(R.string.enter_valid_birthdate), DATE_FORMAT);
            FragmentManager fm = getFragmentManager();
            SimpleTextDialog dialog = SimpleTextDialog.newInstance(message, birthdate);
            dialog.setTargetFragment(this, REQUEST_BIRTHDATE);
            dialog.show(fm, DIALOG_INPUT_TEXT);
            return;
        }

        RetirementOptionsDatabase.getInstance(getContext()).saveBirthdate(birthdate);
        //RetirementOptionsHelper.saveBirthdate(getContext(), birthdate);
        SystemUtils.updateAppWidget(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case MILESTONE_SUMMARY_LOADER:
                uri = RetirementContract.MilestoneSummaryEntry.CONTENT_URI;
                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        null);
                break;
            case STATUS_LOADER:
                loader = new CursorLoader(getActivity(),
                        RetirementContract.TransactionStatusEntry.CONTENT_URI,
                        null,
                        RetirementContract.TransactionStatusEntry.COLUMN_TYPE + " =? ",
                        new String[]{Integer.toString(TRANS_TYPE_MILESTONE_SUMMARY)},
                        null);
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case MILESTONE_SUMMARY_LOADER:
                mMilestoneAdapter.swapCursor(cursor);
                break;
            case STATUS_LOADER:
                if(cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_STATUS);
                    if(statusIndex != -1) {
                        int status = cursor.getInt(statusIndex);
                        if(status == RetirementContract.TransactionStatusEntry.STATUS_UPDATED) {
                            getLoaderManager().initLoader(MILESTONE_SUMMARY_LOADER, null, this);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
