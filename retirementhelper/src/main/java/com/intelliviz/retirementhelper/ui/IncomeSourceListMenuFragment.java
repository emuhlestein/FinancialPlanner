package com.intelliviz.retirementhelper.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;

/**
 * Activity for providing menu.
 *
 * @author Ed Muhlestein
 */
public class IncomeSourceListMenuFragment extends AppCompatActivity {
    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;
    private long mIncomeSourceId;
    private int mIncomeSourceType;

    @Bind(R.id.list_view)
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mIncomeSourceId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        mIncomeSourceType = intent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, -1);

        final String[] incomeActions = getResources().getStringArray(R.array.income_source_actions);
        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, incomeActions);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                sendResult(Activity.RESULT_OK, position);
                finish();
            }
        });
    }

    private void sendResult(int resultCode, int menuItem) {
        Intent intent = new Intent();

        int action = -1;
        if(menuItem == MENU_EDIT) {
            action = INCOME_ACTION_EDIT;
        } else if(menuItem == MENU_DELETE) {
            action = INCOME_ACTION_DELETE;
        }
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, mIncomeSourceType);
        setResult(resultCode, intent);
    }
}
