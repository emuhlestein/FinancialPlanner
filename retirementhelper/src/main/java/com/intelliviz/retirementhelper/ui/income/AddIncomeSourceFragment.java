package com.intelliviz.retirementhelper.ui.income;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;


/**
 * Activity used for adding and editing income sources
 *
 * @author Ed Muhlestein
 */
public class AddIncomeSourceFragment extends Fragment {
    public static final String EDIT_INCOME_FRAG_TAG = "edit income frag tag";
    private Toolbar mToolbar;


    public AddIncomeSourceFragment() {
        // Required empty public constructor
    }

    public static AddIncomeSourceFragment newInstance(long incomeSourceId) {
        AddIncomeSourceFragment fragment = new AddIncomeSourceFragment();
        Bundle args = new Bundle();
        args.putLong(RetirementConstants.EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_add_income_source, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.add_income_source_toolbar);


        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //String []incomeTypes = getResources().getStringArray(R.array.income_types);
        //String incomeSourceType = incomeTypes[mIncomeSourceType];
        //mToolbar.setSubtitle(incomeSourceType);
        return view;
    }
}
