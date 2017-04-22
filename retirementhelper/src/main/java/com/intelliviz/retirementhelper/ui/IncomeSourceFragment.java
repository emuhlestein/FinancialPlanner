package com.intelliviz.retirementhelper.ui;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeSourceAdapter;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class IncomeSourceFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceFragment.class.getSimpleName();
    private static final int SAVINGS_REQUEST = 0;
    private static final int PENSION_REQUEST = 1;
    private static final int GOV_PENSION_REQUEST = 2;
    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.addIncomeTypeFAB) FloatingActionButton mAddIncomeSourceFAB;


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.InstitutionEntry.CONTENT_URI;
        loader = new CursorLoader(getActivity(),
                uri, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mIncomeSourceAdapter.swapCursor(cursor);
        if (mIncomeSourceAdapter.getItemCount() == 0) {
            mEmptyView.setText(R.string.empty_list);
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIncomeSourceAdapter.swapCursor(null);
    }

    public IncomeSourceFragment() {
        // Required empty public constructor
    }

    public static IncomeSourceFragment newInstance() {
        IncomeSourceFragment fragment = new IncomeSourceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(this);
        getLoaderManager().initLoader(INCOME_TYPE_LOADER, null, this);

        mAddIncomeSourceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add new income source", Snackbar.LENGTH_LONG);
                snackbar.show();
                //Intent intent = new Intent(getContext(), AddIncomeSourceActivity.class);
                //startActivityForResult(intent, ADD_INCOME_REQUEST);

                final String[] incomeTypes = getResources().getStringArray(R.array.income_types);

                // TODO wrap in DialogFragment
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select an account type");
                builder.setItems(incomeTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getContext(), "You selected " + incomeTypes[item], Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                        Intent intent = new Intent(getContext(), AddIncomeSourceActivity.class);
                        intent.putExtra(AddIncomeSourceActivity.INCOME_TYPE, item);
                        switch(item) {
                            case RetirementConstants.INCOME_TYPE_SAVINGS:
                                startActivityForResult(intent, SAVINGS_REQUEST);
                                break;
                            case RetirementConstants.INCOME_TYPE_PENSION:
                                startActivityForResult(intent, PENSION_REQUEST);
                                break;
                            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                                startActivityForResult(intent, GOV_PENSION_REQUEST);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setSubtitle("Income Source");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SAVINGS_REQUEST) {

                int incomeSourceType = intent.getIntExtra(AddIncomeSourceActivity.INCOME_TYPE, 0);
                String instituteName = intent.getStringExtra(AddIncomeSourceActivity.INSTITUTE_NAME);
                String balance = intent.getStringExtra(AddIncomeSourceActivity.BALANCE);
                String interest = intent.getStringExtra(AddIncomeSourceActivity.INTEREST);
                String monthlyIncrease = intent.getStringExtra(AddIncomeSourceActivity.MONTHLY_INCREASE);

                //RetirementQueryHandler queryHandler = new RetirementQueryHandler(getContext());
                //queryHandler.setRetirementQueryListener(this);

                Uri uri = RetirementContract.InstitutionEntry.CONTENT_URI;
                String[] projection = {RetirementContract.InstitutionEntry.COLUMN_NAME};
                String selection = RetirementContract.InstitutionEntry.COLUMN_NAME + " = ?";
                String[] selectionArgs = {instituteName};
                Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if(cursor == null || !cursor.moveToFirst()) {
                    // institution does not exist; add it
                    ContentValues values = new ContentValues();
                    values.put(RetirementContract.InstitutionEntry.COLUMN_NAME, instituteName);
                    values.put(RetirementContract.InstitutionEntry.COLUMN_TYPE, incomeSourceType);
                    uri = getContext().getContentResolver().insert(RetirementContract.InstitutionEntry.CONTENT_URI, values);
                    String id = uri.getLastPathSegment();
                    long lid = Long.parseLong(id);

                    float finterest = Float.parseFloat(interest);
                    float fmonthly_increase = Float.parseFloat(monthlyIncrease);

                    values = new ContentValues();
                    values.put(RetirementContract.SavingsDataEntry.COLUMN_INSTITUTION_ID, lid);
                    values.put(RetirementContract.SavingsDataEntry.COLUMN_INTEREST, finterest);
                    values.put(RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION, fmonthly_increase);
                    uri = getContext().getContentResolver().insert(RetirementContract.SavingsDataEntry.CONTENT_URI, values);

                    DateFormat dateFormat = new SimpleDateFormat(RetirementConstants.DATE_FORMAT);
                    Date date = new Date();
                    System.out.println(dateFormat.format(date));
                }
            } else if (requestCode == PENSION_REQUEST) {

            } else if (requestCode == GOV_PENSION_REQUEST) {

            }

            getLoaderManager().restartLoader(INCOME_TYPE_LOADER, null, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSelectIncomeSource(long id) {
        // we have the institute id; being the up the details page
        // The details page will list current institute data.
        Intent intent = new Intent(getContext(), IncomeSourceDetailActivity.class);
        startActivity(intent);
    }
}
