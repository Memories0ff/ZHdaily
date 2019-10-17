package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.NewsSummariesHelper;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;
import com.sion.zhdaily.views.adapters.TopNewsSummaryPagerAdapter;
import com.sion.zhdaily.views.views.NewsSummaryListRecyclerView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends Activity {

    NewsSummaryListRecyclerView rv = null;
    NewsSummaryListRvAdapter rvAdapter = null;

    View headerView = null;
    ViewPager vpTopNews = null;
    TopNewsSummaryPagerAdapter pagerAdapter = null;

    Toolbar tb = null;
    TextView tbTvTitle = null;
    ImageView tbIvMore = null;
    PopupMenu popupMenu = null;

    DrawerLayout dl = null;
    LinearLayout llToIndexBtn = null;

    SwipeRefreshLayout srl = null;

    public NewsSummariesHelper helper = new NewsSummariesHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置DrawerLayout
        dl = findViewById(R.id.dl);
        llToIndexBtn = findViewById(R.id.ll_toIndex);
        llToIndexBtn.setOnClickListener((v) -> update());

        //设置标题栏
        //标题文字
        tb = findViewById(R.id.tb);
        tbTvTitle = findViewById(R.id.tv_tbTitle);
        tb.setNavigationOnClickListener((v) -> {
            dl.openDrawer(Gravity.LEFT);
        });
        //三个点按钮和PopupMenu
        tbIvMore = findViewById(R.id.iv_tbMore);
        popupMenu = new PopupMenu(this, tb, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        //滑动按钮打开菜单
        tbIvMore.setOnTouchListener(popupMenu.getDragToOpenListener());
        popupMenu.setOnMenuItemClickListener((menuItem) -> {
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
        tbIvMore.setOnClickListener((v) -> {
            //弹出popupmenu
            popupMenu.show();
        });

        //下拉刷新
        srl = findViewById(R.id.srl_update);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            update();
            srl.setRefreshing(false);
        });


        //设置显示新闻的ViewPager轮播图和对应adapter
        headerView = getLayoutInflater().inflate(R.layout.rv_summary_header_view, null, false);
        vpTopNews = headerView.findViewById(R.id.vp);
        pagerAdapter = new TopNewsSummaryPagerAdapter(this, vpTopNews, helper.topNewsSummariesList);
        vpTopNews.setAdapter(pagerAdapter);
        pagerAdapter.setLoading(true);

        //设置显示新闻的RecycyerView和对应adapter
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new NewsSummaryListRvAdapter(this, rv, helper.newsSummariesList, headerView);
        rvAdapter.setOnTopViewPagerMoveToTopListener(() -> tbTvTitle.setText("首页"));
        rvAdapter.setOnItemMoveToTopListener((pos) -> tbTvTitle.setText(helper.newsSummariesList.get(pos).getDateStr()));
        rv.setAdapter(rvAdapter);
        rvAdapter.setLoading(true);

        //从网络下载数据并显示
        new Thread(() -> {
            helper.getNewsSummariesDayByDay();
            runOnUiThread(() -> {
                pagerAdapter.notifyDataSetChanged();
                pagerAdapter.setLoading(false);
                pagerAdapter.startTimingPageRoll();
                rvAdapter.notifyNewsSummaryItemInserted(helper.insertRangeStartPosition, helper.loadedNewsSummaryNum);
                rvAdapter.setLoading(false);
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        //左边DrawerLayout打开的情况下则关闭
        if (dl.isDrawerOpen(Gravity.LEFT)) {
            dl.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    private void update() {
        new Thread(() -> {
            pagerAdapter.setLoading(true);
            rvAdapter.setLoading(true);
            if (helper.update()) {
                runOnUiThread(() -> {
                    pagerAdapter.notifyDataSetChanged();
                    pagerAdapter.setLoading(false);
//                    pagerAdapter.startTimingPageRoll();
                    rvAdapter.notifyDataSetChanged();
                    rvAdapter.setLoading(false);
                    //轮播图设为初始位置
                    vpTopNews.setCurrentItem(0);
                    Toast.makeText(this, "更新完成", Toast.LENGTH_SHORT).show();
                    if (dl.isDrawerOpen(Gravity.LEFT)) {
                        dl.closeDrawer(Gravity.LEFT);
                    }
                });
            } else {
                pagerAdapter.setLoading(false);
                rvAdapter.setLoading(false);
                runOnUiThread(() -> Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show());
//                pagerAdapter.startTimingPageRoll();
            }
        }).start();

    }

}
