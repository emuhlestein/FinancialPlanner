package com.intelliviz.retirementhelper.ui.income;


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
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.IncomeSourceData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class IncomeSourceListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private static final int SAVINGS_REQUEST = 0;
    private static final int TAX_DEFERRED_REQUEST = 1;
    private static final int PENSION_REQUEST = 2;
    private static final int GOV_PENSION_REQUEST = 3;

    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyView)
    TextView mEmptyView;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.addIncomeTypeFAB)
    FloatingActionButton mAddIncomeSourceFAB;


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.IncomeSourceEntry.CONTENT_URI;
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

    public IncomeSourceListFragment() {
        // Required empty public constructor
    }

    public static IncomeSourceListFragment newInstance() {
        IncomeSourceListFragment fragment = new IncomeSourceListFragment();
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
        View view = inflater.inflate(R.layout.fragment_income_list_layout, container, false);
        ButterKnife.bind(this, view);

        mIncomeSourceAdapter = new IncomeSourceAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mIncomeSourceAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mIncomeSourceAdapter.setOnSelectIncomeSourceListener(this);
        getLoaderManager().initLoader(INCOME_TYPE_LOADER, null, this);

        // The FAB will pop up an activity to allow a new income source to be created.
        mAddIncomeSourceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add new income source", Snackbar.LENGTH_LONG);
                snackbar.show();

                final String[] incomeTypes = getResources().getStringArray(R.array.income_types);

                // TODO wrap in DialogFragment
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select an account type");
                builder.setItems(incomeTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getContext(), "You selected " + incomeTypes[item], Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                        Intent intent = new Intent(getContext(), IncomeSourceActivity.class);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, item);
                        switch (item) {
                            case RetirementConstants.INCOME_TYPE_SAVINGS:
                                startActivityForResult(intent, SAVINGS_REQUEST);
                                break;
                            case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                                startActivityForResult(intent, TAX_DEFERRED_REQUEST);
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

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setSubtitle("Income Source");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVINGS_REQUEST:
                    saveSavingsData(intent);
                    break;
                case TAX_DEFERRED_REQUEST:
                    break;
                case PENSION_REQUEST:
                    break;

                case GOV_PENSION_REQUEST:
                    break;
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
    public void onSelectIncomeSource(long id, String name) {
        final long incomeSourceId = id;
        final String incomeSourceName = name;
        // TODO wrap in DialogFragment
        final String[] incomeActions = getResources().getStringArray(R.array.income_source_actions);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(incomeActions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                Toast.makeText(getContext(), "You selected " + incomeActions[item], Toast.LENGTH_LONG).show();
                dialogInterface.dismiss();
                Intent intent = new Intent(getContext(), IncomeSourceActivity.class);

                switch (item) {
                    case RetirementConstants.INCOME_ACTION_VIEW:
                        intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, item);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, incomeSourceName);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);
                        startActivityForResult(intent, SAVINGS_REQUEST);
                        break;
                    case RetirementConstants.INCOME_ACTION_EDIT:
                        intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME, incomeSourceName);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, item);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_EDIT);
                        startActivityForResult(intent, SAVINGS_REQUEST);
                        break;
                    case RetirementConstants.INCOME_ACTION_DELETE:

                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveSavingsData(Intent intent) {
        long incomeSourceId = intent.getLongExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
        int incomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, 0);
        String incomeSourceName = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
        String balance = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE);
        String balanceDate = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE_DATE);
        String interest = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST);
        String monthlyIncrease = intent.getStringExtra(RetirementConstants.EXTRA_INCOME_SOURCE_MONTHLY_INCREASE);
        IncomeSourceData isd = DataBaseUtils.getIncomeSourceData(getContext(), incomeSourceName);

        if (isd == null) {
            String sid = DataBaseUtils.addIncomeSource(getContext(), incomeSourceType, incomeSourceName);
            long id = Long.parseLong(sid);

            String sdid = DataBaseUtils.addSavingsData(getContext(), id, monthlyIncrease, interest);
            String bdid = DataBaseUtils.addBalanceData(getContext(), id, balance, balanceDate);
        } else {
            String sid = Long.toString(incomeSourceId);

            int rowsUpdated = DataBaseUtils.saveIncomeSourceData(getContext(), incomeSourceId, incomeSourceName, incomeSourceType);
            if(rowsUpdated != 1) {
                // TODO handle error
            }

            rowsUpdated = DataBaseUtils.saveSavingsData(getContext(), incomeSourceId, monthlyIncrease, interest);
            if(rowsUpdated != 1) {
                // TODO handle error
            }

            rowsUpdated = DataBaseUtils.saveBalanceData(getContext(), incomeSourceId, balance, balanceDate);
            if(rowsUpdated != 1) {
                // TODO handle error
            }
        }
    }
}
