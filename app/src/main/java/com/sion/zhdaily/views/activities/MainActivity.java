package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsSummary;
import com.sion.zhdaily.presenters.NewsSummariesHelper;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    RecyclerView rv = null;
    List<String> contents = new ArrayList<>();
    NewsSummaryListRvAdapter adapter = null;

    List<NewsSummary> topNews = new ArrayList<>();
    List<NewsSummary> latestNews = new ArrayList<>();
    List<NewsSummary> yesterdayNews = new ArrayList<>();
    NewsSummariesHelper helper = new NewsSummariesHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                helper.getNewsSummaries();
            }
        }).start();


        for (int i = 0; i < 30; ++i) {
            contents.add("数据" + (i + 1));
        }
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsSummaryListRvAdapter(this, rv, contents);
        rv.setAdapter(adapter);

    }

}
