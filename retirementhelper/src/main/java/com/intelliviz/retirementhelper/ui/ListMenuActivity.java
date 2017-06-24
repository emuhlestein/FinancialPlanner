package com.intelliviz.retirementhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_EDIT;

public class ListMenuActivity extends AppCompatActivity {
    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;

    @Bind(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menu);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        final String[] incomeActions = getResources().getStringArray(R.array.income_source_actions);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, incomeActions);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Toast.makeText(ListMenuActivity.this, "You selected " + incomeActions[position], Toast.LENGTH_LONG).show();
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
        setResult(resultCode, intent);
    }
}
