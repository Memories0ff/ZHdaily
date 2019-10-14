package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.NewsSummariesHelper;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;
import com.sion.zhdaily.views.adapters.TopNewsSummaryPagerAdapter;
import com.sion.zhdaily.views.views.NewsSummaryListRecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends Activity {

    NewsSummaryListRecyclerView rv = null;
    NewsSummaryListRvAdapter rvAdapter = null;

    View headerView = null;
    ViewPager vp = null;
    TopNewsSummaryPagerAdapter pagerAdapter = null;

    public NewsSummariesHelper helper = new NewsSummariesHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerView = getLayoutInflater().inflate(R.layout.rv_summary_header_view, null, false);
        vp = headerView.findViewById(R.id.vp);
        pagerAdapter = new TopNewsSummaryPagerAdapter(this, helper.topNewsSummariesList);
        vp.setAdapter(pagerAdapter);
        pagerAdapter.setLoading(true);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new NewsSummaryListRvAdapter(this, rv, helper.newsSummariesList, headerView);
        rv.setAdapter(rvAdapter);
        rvAdapter.setLoading(true);

        new Thread(() -> {
            helper.getNewsSummariesDayByDay();
            runOnUiThread(() -> {
                pagerAdapter.notifyDataSetChanged();
                pagerAdapter.setLoading(false);
                rvAdapter.notifyNewsSummaryItemInserted(helper.insertRangeStartPosition, helper.loadedNewsSummaryNum);
                rvAdapter.setLoading(false);
            });
        }).start();

    }

}
