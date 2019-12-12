package com.sion.zhdaily.views.activities;

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
import com.sion.zhdaily.models.beans.NewsSummary;
import com.sion.zhdaily.mvp.presenters.MainPresenter;
import com.sion.zhdaily.mvp.views.IMainView;
import com.sion.zhdaily.utils.base.BaseActivity;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;
import com.sion.zhdaily.views.adapters.TopNewsSummaryPagerAdapter;
import com.sion.zhdaily.views.views.FixedLinearLayoutManager;
import com.sion.zhdaily.views.views.NewsSummaryListRecyclerView;

import java.util.ArrayList;


public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView {


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

    //---------------------------------Activity方法---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPresenter().registerConnMgr();

        //设置DrawerLayout
        dl = findViewById(R.id.dl);
        flToIndexBtn = findViewById(R.id.fl_toIndex);
        flToIndexBtn.setOnClickListener((v) -> {
            uiSwitchToLoading(false);
            getPresenter().update();
        });

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
        srl.setOnRefreshListener(() -> {
            if (!rvAdapter.isLoading()) {
                getPresenter().update();
            }
        });


        //设置显示新闻的ViewPager轮播图和对应adapter
        headerView = getLayoutInflater().inflate(R.layout.rv_summary_header_view, null, false);
        vpTopNews = headerView.findViewById(R.id.vp);
        pagerAdapter = new TopNewsSummaryPagerAdapter(this, vpTopNews, (ArrayList<NewsSummary>) getPresenter().getTopNewsDataSource());
        vpTopNews.setAdapter(pagerAdapter);
        vpTopNews.setOffscreenPageLimit(1);

        //设置显示新闻的RecyclerView和对应adapter
        rv = findViewById(R.id.rv);
        //数据更新时不可滑动
        rv.setOnTouchListener((view, motionEvent) -> srl.isRefreshing());
        FixedLinearLayoutManager fixedLinearLayoutManager = new FixedLinearLayoutManager(this);
        fixedLinearLayoutManager.setItemPrefetchEnabled(false);
        rv.setLayoutManager(fixedLinearLayoutManager);
        rvAdapter = new NewsSummaryListRvAdapter(this, rv, getPresenter().getSummariesDataSource(), headerView);
        rvAdapter.setOnTopViewPagerMoveToTopListener(() -> tbTvTitle.setText("首页"));
        rvAdapter.setOnItemMoveToTopListener((pos) -> {
            tbTvTitle.setText(getPresenter().getSummariesDataSource().get(pos).getDateStr());
        });
        rv.setAdapter(rvAdapter);

        getPresenter().loadInitialData();

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
        getPresenter().unRegisterConnMgr();
        super.onDestroy();
    }

    //---------------------------------抽象类方法实现---------------------------------

    @Override
    protected IMainView createView() {
        return this;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    //---------------------------------IMainView接口方法---------------------------------
    @Override
    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void insertForSummaries() {
        rvAdapter.notifyNewsSummaryItemInserted(getPresenter().getInsertRangeStartPosition(), getPresenter().getLoadedNewsSummaryNum());
    }

    @Override
    public void updateForInsert() {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateForChangeAll() {
        pagerAdapter.notifyDataSetChanged();
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void uiSwitchToLoading(boolean isContinue) {
        pagerAdapter.setLoading(true);
        rvAdapter.setLoading(true);
        if (!isContinue) {
            srl.setRefreshing(true);
            pagerAdapter.stopTimingPageRoll();
        }
        if (dl.isDrawerOpen(Gravity.LEFT)) {
            dl.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void uiSwitchToNotLoading() {
        //轮播图设为初始位置
        vpTopNews.setCurrentItem(0);
        pagerAdapter.setLoading(false);
        pagerAdapter.startTimingPageRoll();
        srl.setRefreshing(false);
        rvAdapter.setLoading(false);
        if (dl.isDrawerOpen(Gravity.LEFT)) {
            dl.closeDrawer(Gravity.LEFT);
        }
    }

}
