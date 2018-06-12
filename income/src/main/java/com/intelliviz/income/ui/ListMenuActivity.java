package com.intelliviz.income.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intelliviz.income.R;

import static com.intelliviz.income.util.RetirementConstants.EXTRA_MENU_ITEM_LIST;
import static com.intelliviz.income.util.RetirementConstants.EXTRA_SELECTED_MENU_ITEM;


/**
 * Activity for providing menu.
 *
 * @author Ed Muhlestein
 */
public class ListMenuActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menu);

        mListView = findViewById(R.id.list_view);

        Intent intent = getIntent();
        String[] actions = intent.getStringArrayExtra(EXTRA_MENU_ITEM_LIST);
        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, actions);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                sendResult(Activity.RESULT_OK, position);
                finish();
                overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_out);
            }
        });
    }

    private void sendResult(int resultCode, int menuItem) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_MENU_ITEM, menuItem);
        setResult(resultCode, intent);
    }
}
