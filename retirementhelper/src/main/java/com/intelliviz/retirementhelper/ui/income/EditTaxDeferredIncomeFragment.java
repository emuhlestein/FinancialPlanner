package com.intelliviz.retirementhelper.ui.income;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.services.TaxDeferredIntentService;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding tax deferred income sources.
 *
 * @author Ed Muhlestein
 */
public class EditTaxDeferredIncomeFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditTaxDeferredIncomeFragment.class.getSimpleName();
    public static final String EDIT_TAXDEF_INCOME_FRAG_TAG = "edit taxdef income frag tag";
    private TaxDeferredIncomeData mTDID;
    public static final int TDID_LOADER = 0;
    public static final int STATUS_LOADER = 1;
    private long mId;
    private TaxDeferredAsyncHandler mTaxDeferredAsyncHandler;
    private TaxDeferredStatusAsyncHandler mTaxDeferredStatusAsyncHandler;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.name_edit_text)
    EditText mIncomeSourceName;

    @Bind(R.id.balance_text)
    EditText mBalance;

    @Bind(R.id.annual_interest_text)
    EditText mAnnualInterest;

    @Bind(R.id.monthly_increase_text)
    EditText mMonthlyIncrease;

    @Bind(R.id.penalty_age_text)
    EditText mPenaltyAge;

    @Bind(R.id.penalty_amount_text)
    EditText mPenaltyAmount;

    @OnClick(R.id.add_income_source_button) void onAddIncomeSource() {
        updateIncomeSourceData();
        getActivity().finish();
    }

    public EditTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static EditTaxDeferredIncomeFragment newInstance(Intent intent) {
        EditTaxDeferredIncomeFragment fragment = new EditTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            mId = -1;
            if(intent != null) {
                mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        mTDID = null;

        if(mId != -1) {
            Bundle bundle = new Bundle();
            bundle.putString(ID_ARGS, Long.toString(mId));
            getLoaderManager().initLoader(TDID_LOADER, bundle, this);
        }

        getLoaderManager().initLoader(STATUS_LOADER, null, this);

        mTaxDeferredAsyncHandler = new TaxDeferredAsyncHandler(getActivity().getContentResolver());
        mTaxDeferredStatusAsyncHandler = new TaxDeferredStatusAsyncHandler(getActivity().getContentResolver());

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
                    }
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyIncrease.setText(formattedString);
                    }
                }
            }
        });

        mAnnualInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mAnnualInterest.setText(interest);
                    } else {
                        mAnnualInterest.setText("");
                    }
                }
            }
        });

        mPenaltyAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        interest += "%";
                        mPenaltyAmount.setText(interest);
                    } else {
                        mPenaltyAmount.setText("");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateUI() {
        if (mTDID == null || mTDID.getId() == -1) {
            return;
        }

        String incomeSourceName = mTDID.getName();
        int type = mTDID.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);
        SystemUtils.setToolbarSubtitle(getActivity(), incomeSourceTypeString);

        String balanceString;
        double balance = mTDID.getBalance();
        balanceString = SystemUtils.getFormattedCurrency(balance);

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mTDID.getMonthAddition());
        String minimumAge = mTDID.getMinimumAge();
        AgeData age = SystemUtils.parseAgeString(minimumAge);
        minimumAge = SystemUtils.getFormattedAge(age);

        String penaltyAmount = mTDID.getPenalty() + "%";

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(incomeSourceTypeString);
        }
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);

        String interest = mTDID.getInterestRate()+"%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(monthlyIncreaseString);
        mPenaltyAge.setText(minimumAge);
        mPenaltyAmount.setText(penaltyAmount);
    }

    public void updateIncomeSourceData() {
        String value = mBalance.getText().toString();
        String balance = getFloatValue(value);
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.balance_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualInterest.getText().toString();
        String interest = getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.interest_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyIncrease = getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mPenaltyAmount.getText().toString();
        String penaltyAmount = getFloatValue(value);
        if(penaltyAmount == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.value_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String minimumAge = mPenaltyAge.getText().toString();
        minimumAge = SystemUtils.trimAge(minimumAge);
        AgeData minAge = SystemUtils.parseAgeString(minimumAge);
        if(minAge == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.age_not_valid) + " " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        String name = mIncomeSourceName.getText().toString();
        String date = SystemUtils.getTodaysDate();

        double annualInterest = Double.parseDouble(interest);
        double increase = Double.parseDouble(monthlyIncrease);
        double penalty = Double.parseDouble(penaltyAmount);
        double dbalance = Double.parseDouble(balance);
        TaxDeferredIncomeData tdid = new TaxDeferredIncomeData(mId, name, INCOME_TYPE_TAX_DEFERRED, minimumAge, annualInterest, increase, penalty, dbalance, 1);
        /*
        if(mId == -1) {
            mTaxDeferredAsyncHandler.insert(name, INCOME_TYPE_TAX_DEFERRED, minimumAge, increase,
                    annualInterest, penalty, dbalance);
        } else {
            mTaxDeferredAsyncHandler.update(mId, name, minimumAge, increase,
                    annualInterest, penalty, dbalance);
        }
        */
        updateTDID(tdid);
    }

    private void updateTDID(TaxDeferredIncomeData tdid) {
        Intent intent = new Intent(getContext(), TaxDeferredIntentService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_ID, tdid.getId());
        intent.putExtra(EXTRA_DB_DATA, tdid);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        getActivity().startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case TDID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
                if(uri != null) {
                    uri = Uri.withAppendedPath(uri, id);
                }

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
                        new String[]{Integer.toString(INCOME_TYPE_TAX_DEFERRED)},
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
            case TDID_LOADER:
                mTDID = TaxDeferredHelper.extractData(cursor);
                updateUI();
                break;
            case STATUS_LOADER:
                if(cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_STATUS);
                    int resultIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_RESULT);
                    int actionIndex = cursor.getColumnIndex(RetirementContract.TransactionStatusEntry.COLUMN_ACTION);
                    if(statusIndex != -1) {
                        int status = cursor.getInt(statusIndex);
                        if(status == RetirementContract.TransactionStatusEntry.STATUS_UPDATED) {
                            if(actionIndex != -1 && resultIndex != -1) {
                                int action = cursor.getInt(actionIndex);
                                int numRows;
                                switch(action) {
                                    case RetirementContract.TransactionStatusEntry.ACTION_DELETE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " deleted");
                                        break;
                                    case RetirementContract.TransactionStatusEntry.ACTION_UPDATE:
                                        numRows = Integer.parseInt(cursor.getString(resultIndex));
                                        Log.d(TAG, numRows + " update");
                                        break;
                                    case RetirementContract.TransactionStatusEntry.ACTION_INSERT:
                                        String uri = cursor.getString(resultIndex);
                                        Log.d(TAG, uri + " inserted");
                                        break;
                                }
                            }
                            mTaxDeferredStatusAsyncHandler.clear();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public static class TaxDeferredStatusAsyncHandler extends AsyncQueryHandler {

        public TaxDeferredStatusAsyncHandler(ContentResolver cr) {
            super(cr);
        }

        public void clear() {
            Uri uri = RetirementContract.TransactionStatusEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(RetirementContract.TransactionStatusEntry.COLUMN_STATUS,
                    RetirementContract.TransactionStatusEntry.STATUS_NONE);
            values.put(RetirementContract.TransactionStatusEntry.COLUMN_ACTION,
                    RetirementContract.TransactionStatusEntry.ACTION_NONE);
            values.put(RetirementContract.TransactionStatusEntry.COLUMN_RESULT, "");
            startUpdate(0, null, uri, values, null, null);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if(result != 1) {
                Log.d(TAG, "Error updating status");
            }
        }
    }

    public static class IncomeSourceAsyncHandler extends AsyncQueryHandler {
        private InsertCompleteListener mInsertCompleteListener;
        interface InsertCompleteListener {
            void onInsertComplete(Uri uri);
            void onUpdateComplete(int result);
        }

        public IncomeSourceAsyncHandler(ContentResolver cr, InsertCompleteListener insertCompleteListener) {
            super(cr);
            mInsertCompleteListener = insertCompleteListener;
        }

        public void insert(String name, int type) {
            Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, name);
            values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, type);
            startInsert(0, null, uri, values);
        }

        public void update(long id, String name) {

            Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
            uri = Uri.withAppendedPath(uri, Long.toString(id));
            ContentValues values = new ContentValues();
            values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, name);
            String selection = RetirementContract.IncomeTypeEntry._ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(id)};
            startUpdate(0, null, uri, values, selection, selectionArgs);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if(mInsertCompleteListener != null) {
                mInsertCompleteListener.onInsertComplete(uri);
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if(mInsertCompleteListener != null) {
                mInsertCompleteListener.onUpdateComplete(result);
            }
        }
    }

    public static class TaxDeferredAsyncHandler extends AsyncQueryHandler implements
            IncomeSourceAsyncHandler.InsertCompleteListener {
        private long mId;
        private String mMinAge;
        private double mMonthlyAddition;
        private double mInterest;
        private double mPenalty;
        private double mBalance;

        final WeakReference<ContentResolver> mResolver;

        public TaxDeferredAsyncHandler(ContentResolver cr) {
            super(cr);
            mResolver = new WeakReference<ContentResolver>(cr);
        }

        public void update(long id, String name, String minAge, double monthlyAddition,
                           double interest, double penalty, double balance) {
            mId = id;
            mMinAge = minAge;
            mMonthlyAddition = monthlyAddition;
            mInterest = interest;
            mPenalty = penalty;
            mBalance = balance;
            final ContentResolver resolver = mResolver.get();
            IncomeSourceAsyncHandler incomeSourceAsyncHandler = new IncomeSourceAsyncHandler(resolver, this);
            incomeSourceAsyncHandler.update(id, name);
        }

        public void insert(String name, int type, String minAge, double monthlyAddition,
                           double interest, double penalty, double balance) {
            mMinAge = minAge;
            mMonthlyAddition = monthlyAddition;
            mInterest = interest;
            mPenalty = penalty;
            mBalance = balance;
            final ContentResolver resolver = mResolver.get();
            IncomeSourceAsyncHandler incomeSourceAsyncHandler = new IncomeSourceAsyncHandler(resolver, this);
            incomeSourceAsyncHandler.insert(name, type);
        }

        @Override
        public void onInsertComplete(Uri uri) {
            String incomeId = uri.getLastPathSegment();
            long id = Long.parseLong(incomeId);
            Uri taxDefUri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID, id);
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, mMinAge);
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(mInterest));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(mMonthlyAddition));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(mPenalty));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(mBalance));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, 1);
            startInsert(0, null, taxDefUri, values);
        }

        @Override
        public void onUpdateComplete(int result) {
            Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
            uri = Uri.withAppendedPath(uri, Long.toString(mId));
            ContentValues values = new ContentValues();
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, mMinAge);
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(mInterest));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(mMonthlyAddition));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(mPenalty));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(mBalance));
            values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, 1);
            String selection = RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(mId)};
            startUpdate(0, null, uri, values, selection, selectionArgs);
        }
    }
}
