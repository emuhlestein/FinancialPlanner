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

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;

public class ListMenuActivity extends AppCompatActivity {
    @Bind(R.id.list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menu);
        ButterKnife.bind(this);

        final String[] incomeActions = getResources().getStringArray(R.array.age_actions);
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
        intent.putExtra(EXTRA_SELECTED_MENU_ITEM, menuItem);
        setResult(resultCode, intent);
    }
}
