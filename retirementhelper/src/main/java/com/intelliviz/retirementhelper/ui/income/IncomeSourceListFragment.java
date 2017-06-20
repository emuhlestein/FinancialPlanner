package com.intelliviz.retirementhelper.ui.income;


import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.SummaryData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.GovPensionDataService;
import com.intelliviz.retirementhelper.services.PensionDataService;
import com.intelliviz.retirementhelper.services.TaxDeferredIntentService;
import com.intelliviz.retirementhelper.ui.BirthdateDialog;
import com.intelliviz.retirementhelper.ui.IncomeSourceListMenuFragment;
import com.intelliviz.retirementhelper.ui.YesNoDialog;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.GovPensionHelper;
import com.intelliviz.retirementhelper.util.PensionHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SavingsHelper;
import com.intelliviz.retirementhelper.util.SelectIncomeSourceListener;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;
import com.intelliviz.retirementhelper.widget.WidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.getRetirementOptionsData;
import static com.intelliviz.retirementhelper.util.PensionHelper.addPensionData;
import static com.intelliviz.retirementhelper.util.PensionHelper.getPensionIncomeData;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_PERSONALINFODATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_RETIREOPTIONS_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_VIEW;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_INCOME_MENU;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_YES_NO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;

public class IncomeSourceListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SelectIncomeSourceListener {
    public static final String TAG = IncomeSourceListFragment.class.getSimpleName();
    private static final String DIALOG_MENU = "DialogIncomeMenu";
    private static final String DIALOG_YES_NO = "DialogYesNo";
    private static final String DIALOG_BIRTHDATE = "DialogBirthdate";

    private IncomeSourceAdapter mIncomeSourceAdapter;
    private static final int INCOME_TYPE_LOADER = 0;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.addIncomeTypeFAB) FloatingActionButton mAddIncomeSourceFAB;

    private BroadcastReceiver mSavingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
            RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
            Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, tdid.getId());
            newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, tdid.getType());
            newIntent.putExtra(EXTRA_INCOME_DATA, tdid);
            newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
            startActivityForResult(newIntent, REQUEST_TAX_DEFERRED);
        }
    };

    private BroadcastReceiver mTaxDeferredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
            RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
            Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, tdid.getId());
            newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, tdid.getType());
            newIntent.putExtra(EXTRA_INCOME_DATA, tdid);
            newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
            startActivityForResult(newIntent, REQUEST_TAX_DEFERRED);
        }
    };

    private BroadcastReceiver mPensionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PensionIncomeData pid = intent.getParcelableExtra(EXTRA_DB_DATA);
            RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
            Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, pid.getId());
            newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, pid.getType());
            newIntent.putExtra(EXTRA_INCOME_DATA, pid);
            newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
            startActivityForResult(newIntent, REQUEST_PENSION);
        }
    };

    private BroadcastReceiver mGovPensionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GovPensionIncomeData gpid = intent.getParcelableExtra(EXTRA_DB_DATA);
            RetirementOptionsData rod = intent.getParcelableExtra(EXTRA_DB_EXTRA_DATA);
            Intent newIntent = new Intent(getContext(), IncomeSourceActivity.class);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ID, gpid.getId());
            newIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, gpid.getType());
            newIntent.putExtra(EXTRA_INCOME_DATA, gpid);
            newIntent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
            newIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
            startActivityForResult(newIntent, REQUEST_PENSION);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
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
                        Intent intent = new Intent(getActivity(), IncomeSourceActivity.class);
                        intent.putExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ID, -1);
                        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_ADD);
                        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, item);
                        switch (item) {
                            case INCOME_TYPE_SAVINGS:
                                intent.putExtra(EXTRA_INCOME_DATA, new SavingsIncomeData(INCOME_TYPE_SAVINGS));
                                startActivityForResult(intent, REQUEST_SAVINGS);
                                break;
                            case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                                intent.putExtra(EXTRA_INCOME_DATA, new TaxDeferredIncomeData(RetirementConstants.INCOME_TYPE_TAX_DEFERRED));
                                startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                                break;
                            case RetirementConstants.INCOME_TYPE_PENSION:
                                intent.putExtra(EXTRA_INCOME_DATA, new PensionIncomeData(RetirementConstants.INCOME_TYPE_PENSION));
                                startActivityForResult(intent, REQUEST_PENSION);
                                break;
                            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                                intent.putExtra(EXTRA_INCOME_DATA, new GovPensionIncomeData(RetirementConstants.INCOME_TYPE_GOV_PENSION));
                                startActivityForResult(intent, REQUEST_GOV_PENSION);
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

        FragmentManager fm = getActivity().getSupportFragmentManager();
        BirthdateDialog dialog = BirthdateDialog.newInstance("Please enter your birth date");
        dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_BIRTHDATE);
        dialog.show(fm, DIALOG_BIRTHDATE);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SAVINGS:
                    saveSavingsIncomeData(intent);
                    break;
                case REQUEST_TAX_DEFERRED:
                    saveTaxDeferredData(intent);
                    break;
                case REQUEST_PENSION:
                    savePensionData(intent);
                    break;
                case REQUEST_GOV_PENSION:
                    saveGovPensionData(intent);
                    break;
                case REQUEST_INCOME_MENU:
                    onHandleAction(intent);
                    break;
                case REQUEST_YES_NO:
                    onHandleYesNo(intent);
                    break;
                case REQUEST_BIRTHDATE:
                    onHandleBirthdate(intent);
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
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onSelectIncomeSource(long id, int type, boolean showMenu) {
        final long incomeSourceId = id;
        final int incomeSourceType = type;
        if(showMenu) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            IncomeSourceListMenuFragment dialog = IncomeSourceListMenuFragment.newInstance(id, type);
            dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_INCOME_MENU);
            dialog.show(fm, DIALOG_MENU);
        } else {
            Intent intent;
            RetirementOptionsData rod;
            switch(incomeSourceType) {
                case INCOME_TYPE_SAVINGS:
                    SavingsIncomeData sid = SavingsHelper.getSavingsIncomeData(getContext(), incomeSourceId);
                    rod = getRetirementOptionsData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, sid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, sid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_SAVINGS);
                    break;
                case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                    TaxDeferredIncomeData tdid = TaxDeferredHelper.getTaxDeferredIncomeData(getContext(), incomeSourceId);
                    rod = getRetirementOptionsData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, tdid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, tdid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    break;
                case RetirementConstants.INCOME_TYPE_PENSION:
                    PensionIncomeData pid = getPensionIncomeData(getContext(), incomeSourceId);
                    rod = getRetirementOptionsData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, pid);
                    intent.putExtra(EXTRA_RETIREOPTIONS_DATA, rod);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, pid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    break;
                case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                    GovPensionIncomeData gpid = GovPensionHelper.getGovPensionIncomeData(getContext(), incomeSourceId);
                    PersonalInfoData perId = DataBaseUtils.getPersonalInfoData(getContext());
                    intent = new Intent(getActivity(), IncomeSourceActivity.class);
                    intent.putExtra(EXTRA_INCOME_DATA, gpid);
                    intent.putExtra(EXTRA_PERSONALINFODATA, perId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, gpid.getType());
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
                    startActivityForResult(intent, REQUEST_TAX_DEFERRED);
                    break;
            }
        }
    }

    private void registerReceiver() {
        registerSavingsReceiver();
        registerTaxDeferredReceiver();
        registerPensionReceiver();
        registerGovPensionReceiver();
    }

    private void unregisterReceiver() {
        unregisterSavingsReceiver();
        unregisterTaxDeferredReceiver();
        unregisterPensionReceiver();
        unregisterGovPensionReceiver();
    }

    private void registerSavingsReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_SAVINGS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSavingsReceiver, filter);
    }

    private void unregisterSavingsReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSavingsReceiver);
    }

    private void registerTaxDeferredReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_TAX_DEFERRED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mTaxDeferredReceiver, filter);
    }

    private void unregisterTaxDeferredReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTaxDeferredReceiver);
    }

    private void registerPensionReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_PENSION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPensionReceiver, filter);
    }

    private void unregisterPensionReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPensionReceiver);
    }

    private void registerGovPensionReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_GOV_PENSION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mGovPensionReceiver, filter);
    }

    private void unregisterGovPensionReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mGovPensionReceiver);
    }

    private void onHandleAction(Intent resultIntent) {
        int action = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_VIEW);
        int incomeSourceType = resultIntent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, INCOME_TYPE_SAVINGS);
        long incomeSourceId = resultIntent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
        if(incomeSourceId < 0) {
            return;
        }
        Intent intent = new Intent(getContext(), IncomeSourceActivity.class);

        switch(incomeSourceType) {
            case INCOME_TYPE_SAVINGS:
                if(action == INCOME_ACTION_EDIT) {
                    SavingsIncomeData sid = SavingsHelper.getSavingsIncomeData(getContext(), incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
                    intent.putExtra(EXTRA_INCOME_DATA, sid);
                    intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
                    intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_EDIT);
                    startActivityForResult(intent, REQUEST_SAVINGS);
                } else if(action == INCOME_ACTION_DELETE) {
                    int rowsDeleted = SavingsHelper.deleteSavingsIncome(getContext(), incomeSourceId);
                }
                break;
            case RetirementConstants.INCOME_TYPE_TAX_DEFERRED:
                if(action == INCOME_ACTION_EDIT) {
                    Intent localIntent = new Intent(getContext(), TaxDeferredIntentService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    YesNoDialog dialog = YesNoDialog.newInstance(incomeSourceId);
                    dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_YES_NO);
                    dialog.show(fm, DIALOG_YES_NO);

                }
                break;
            case RetirementConstants.INCOME_TYPE_PENSION:
                if(action == INCOME_ACTION_EDIT) {
                    Intent localIntent = new Intent(getContext(), PensionDataService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    YesNoDialog dialog = YesNoDialog.newInstance(incomeSourceId);
                    dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_YES_NO);
                    dialog.show(fm, DIALOG_YES_NO);
                }
                break;
            case RetirementConstants.INCOME_TYPE_GOV_PENSION:
                if(action == INCOME_ACTION_EDIT) {
                    Intent localIntent = new Intent(getContext(), GovPensionDataService.class);
                    localIntent.putExtra(EXTRA_DB_ID, incomeSourceId);
                    localIntent.putExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
                    getActivity().startService(localIntent);
                } else if(action == INCOME_ACTION_DELETE) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    YesNoDialog dialog = YesNoDialog.newInstance(incomeSourceId);
                    dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_YES_NO);
                    dialog.show(fm, DIALOG_YES_NO);
                }
                break;
        }
    }

    private void onHandleYesNo(Intent intent) {
        int action = intent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, -1);
        if(action == INCOME_ACTION_DELETE) {
            long incomeSourceId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            if(incomeSourceId != -1) {
                int rowsDeleted = TaxDeferredHelper.deleteTaxDeferredIncome(getContext(), incomeSourceId);
                updateAppWidget();
            }
        }
    }

    private void onHandleBirthdate(Intent intent) {
        String birthdate = intent.getStringExtra(EXTRA_BIRTHDATE);
        if(!SystemUtils.validateBirthday(birthdate)) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BirthdateDialog dialog = BirthdateDialog.newInstance("Please enter a valid birth date");
            dialog.setTargetFragment(IncomeSourceListFragment.this, REQUEST_BIRTHDATE);
            dialog.show(fm, DIALOG_BIRTHDATE);
        }
    }

    private void saveSavingsIncomeData(Intent intent) {
        SavingsIncomeData sid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (sid.getId() == -1) {
            String rc = SavingsHelper.addSavingsIncome(getContext(), sid);
        } else {
            SavingsHelper.saveSavingsIncomeData(getContext(), sid);
        }

        updateAppWidget();
    }

    private void saveTaxDeferredData(Intent intent) {
        TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (tdid.getId() == -1) {
            String rc = TaxDeferredHelper.addTaxDeferredIncome(getContext(), tdid);
        } else {
            TaxDeferredHelper.saveTaxDeferredData(getContext(), tdid);
        }

        updateAppWidget();
    }

    private void savePensionData(Intent intent) {
        PensionIncomeData pid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (pid.getId() == -1) {
            String rc = addPensionData(getContext(), pid);
        } else {
            PensionHelper.savePensionData(getContext(), pid);
        }

        updateAppWidget();
    }

    private void saveGovPensionData(Intent intent) {
        GovPensionIncomeData gpid = intent.getParcelableExtra(EXTRA_INCOME_DATA);

        if (gpid.getId() == -1) {
            String rc = GovPensionHelper.addGovPensionData(getContext(), gpid);
        } else {
            GovPensionHelper.saveGovPensionData(getContext(), gpid);
        }

        updateAppWidget();
    }

    private void updateAppWidget() {
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(getContext());
        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(getContext());
        List<MilestoneData> milestones = BenefitHelper.getAllMilestones(getContext(), rod, pid);

        List<SummaryData> summaryData = getSummaryData(milestones);
        DataBaseUtils.updateSummaryData(getContext());

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        ComponentName appWidget = new ComponentName(getContext(), WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    private List<SummaryData> getSummaryData(List<MilestoneData> milestoneData) {
        List<SummaryData> summaryData = new ArrayList<>();
        for(MilestoneData msd : milestoneData) {
            summaryData.add(new SummaryData(msd.getStartAge().toString(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
        return summaryData;
    }
}
