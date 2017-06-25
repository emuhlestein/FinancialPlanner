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

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_MILESONTE_AGE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.MILESTONE_AGE_DELETE;

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

        final String[] incomeActions = getResources().getStringArray(R.array.age_actions);
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
        intent.putExtra(EXTRA_MILESONTE_AGE_ACTION, MILESTONE_AGE_DELETE);
        setResult(resultCode, intent);
    }
}
