package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.NewsSummariesHelper;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;
import com.sion.zhdaily.views.adapters.TopNewsSummaryPagerAdapter;
import com.sion.zhdaily.views.views.FixedLinearLayoutManager;
import com.sion.zhdaily.views.views.NewsSummaryListRecyclerView;


public class MainActivity extends Activity {

    private boolean isNetworkConnected = false;
    private ConnectivityManager connMgr = null;
    private NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    NewsSummaryListRecyclerView rv = null;
    NewsSummaryListRvAdapter rvAdapter = null;

    View headerView = null;
    ViewPager vpTopNews = null;
    TopNewsSummaryPagerAdapter pagerAdapter = null;

    Toolbar tb = null;
    TextView tbTvTitle = null;

    DrawerLayout dl = null;
    FrameLayout flToIndexBtn = null;

    SwipeRefreshLayout srl = null;

    public NewsSummariesHelper helper = new NewsSummariesHelper();


    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //网络状态
        connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connMgr.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isNetworkConnected = true;
        }

        //设置DrawerLayout
        dl = findViewById(R.id.dl);
        flToIndexBtn = findViewById(R.id.fl_toIndex);
        flToIndexBtn.setOnClickListener((v) -> update());

        //设置标题栏
        //标题文字
        tb = findViewById(R.id.tb);
        tbTvTitle = findViewById(R.id.tv_tbTitle);
        tb.setNavigationOnClickListener((v) -> {
            dl.openDrawer(Gravity.LEFT);
        });
        tb.inflateMenu(R.menu.popup_menu);
        tb.setOnMenuItemClickListener((menuItem) -> {
            switch (menuItem.getItemId()) {
                case R.id.switchMode:
                    Toast.makeText(this, "切换模式", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.setting:
                    Toast.makeText(this, "设置选项", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });

        //下拉刷新
        srl = findViewById(R.id.srl_update);
        srl.setOnRefreshListener(refreshListener);


        //设置显示新闻的ViewPager轮播图和对应adapter
        headerView = getLayoutInflater().inflate(R.layout.rv_summary_header_view, null, false);
        vpTopNews = headerView.findViewById(R.id.vp);
        pagerAdapter = new TopNewsSummaryPagerAdapter(this, vpTopNews, helper.topNewsSummariesList);
        vpTopNews.setAdapter(pagerAdapter);
        vpTopNews.setOffscreenPageLimit(1);
        pagerAdapter.setLoading(true);

        //设置显示新闻的RecycyerView和对应adapter
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new FixedLinearLayoutManager(this));
        rvAdapter = new NewsSummaryListRvAdapter(this, rv, helper.newsSummariesList, headerView);
        rvAdapter.setOnTopViewPagerMoveToTopListener(() -> tbTvTitle.setText("首页"));
        rvAdapter.setOnItemMoveToTopListener((pos) -> tbTvTitle.setText(helper.newsSummariesList.get(pos).getDateStr()));
        rv.setAdapter(rvAdapter);
        rvAdapter.setLoading(true);

        //从网络下载数据并显示
        if (isNetworkConnected) {
            new Thread(() -> {
                helper.getNewsSummariesDayByDay();
                runOnUiThread(() -> {
                    pagerAdapter.notifyDataSetChanged();
                    pagerAdapter.setLoading(false);
                    pagerAdapter.startTimingPageRoll();
                    rvAdapter.notifyNewsSummaryItemInserted(helper.insertRangeStartPosition, helper.loadedNewsSummaryNum);
                });
                helper.getNewsSummariesDayByDay();
                runOnUiThread(() -> {
                    rvAdapter.notifyNewsSummaryItemInserted(helper.insertRangeStartPosition, helper.loadedNewsSummaryNum);
                    rvAdapter.setLoading(false);
                });
            }).start();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
        srl.setRefreshing(true);
        update();
        srl.setRefreshing(false);
    };


    private void update() {
        if (isNetworkConnected) {
            new Thread(updateSummary).start();
        } else {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    //更新新闻列表操作Runnable
    private Runnable updateSummary = () -> {
        pagerAdapter.setLoading(true);
        rvAdapter.setLoading(true);
        if (helper.update()) {
            runOnUiThread(() -> {
                rvAdapter.notifyDataSetChanged();
                rvAdapter.setLoading(false);

                pagerAdapter.stopTimingPageRoll();
                pagerAdapter.notifyDataSetChanged();
                //轮播图设为初始位置
                vpTopNews.setCurrentItem(0);
                pagerAdapter.startTimingPageRoll();
                pagerAdapter.setLoading(false);

                Toast.makeText(MainActivity.this, "更新完成", Toast.LENGTH_SHORT).show();
                if (dl.isDrawerOpen(Gravity.LEFT)) {
                    dl.closeDrawer(Gravity.LEFT);
                }
            });
        } else {
            pagerAdapter.setLoading(false);
            rvAdapter.setLoading(false);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
//                pagerAdapter.startTimingPageRoll();
        }

    };

    @Override
    public void onBackPressed() {
        //左边DrawerLayout打开的情况下则关闭
        if (dl.isDrawerOpen(Gravity.LEFT)) {
            dl.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        if (pagerAdapter != null) {
            pagerAdapter.startTimingPageRoll();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止轮播
        if (pagerAdapter != null) {
            pagerAdapter.stopTimingPageRoll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connMgr.unregisterNetworkCallback(networkCallback);
    }

    class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
//            Toast.makeText(MainActivity.this, "网络已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
//            Toast.makeText(MainActivity.this, "网络已断开", Toast.LENGTH_SHORT).show();
        }
    }
}
