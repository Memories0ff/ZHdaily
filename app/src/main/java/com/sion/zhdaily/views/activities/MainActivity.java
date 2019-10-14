package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.NewsSummariesHelper;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;
import com.sion.zhdaily.views.views.NewsSummaryListRecyclerView;


public class MainActivity extends Activity {

    NewsSummaryListRecyclerView rv = null;
    NewsSummaryListRvAdapter adapter = null;
    public NewsSummariesHelper helper = new NewsSummariesHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsSummaryListRvAdapter(this, rv, helper.newsSummariesList, R.layout.rv_summary_header_view);
        rv.setAdapter(adapter);
        adapter.setLoading(true);

        new Thread(() -> {
            helper.getNewsSummariesDayByDay();
            runOnUiThread(() -> {
                adapter.notifyNewsSummaryItemInserted(helper.insertRangeStartPosition, helper.loadedNewsSummaryNum);
                adapter.setLoading(false);
            });
        }).start();

    }

}
