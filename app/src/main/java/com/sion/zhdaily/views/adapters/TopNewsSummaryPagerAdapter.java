package com.sion.zhdaily.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsSummary;
import com.sion.zhdaily.views.activities.MainActivity;
import com.sion.zhdaily.views.activities.NewsContentActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TopNewsSummaryPagerAdapter extends PagerAdapter {

    private Context mContext = null;
    private List<NewsSummary> mContents = null;
    private ViewPager vp = null;
    private List<View> mViews = new ArrayList<>();

    //计时
    private Timer timer = null;
    private TimerTask timerTask = null;

    //是否正在加载内容
    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public TopNewsSummaryPagerAdapter(Context mContext, ViewPager vp, List<NewsSummary> mContent) {
        this.mContext = mContext;
        this.vp = vp;
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTimingPageRoll();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.mContents = mContent;
    }

    @Override
    public int getCount() {
        return mContents.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //????????????????????√
//        container.removeView(mViewList.get(position));
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        loadViews();
        super.notifyDataSetChanged();
    }

    private void loadViews() {
        mViews.clear();
        for (int i = 0; i < mContents.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rv_top_view_pager_item_view, vp, false);
            ImageView imageView = view.findViewById(R.id.iv_topNewsPic);
            //防止在挂靠的Activity已被销毁的情况下使用Glide
            if (!((MainActivity) mContext).isDestroyed()) {
                Glide.with(mContext)
                        .load(mContents.get(i).getImageUrl())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imageView);
            }
            TextView textView = view.findViewById(R.id.tv_topNewsTitle);
            textView.setText(mContents.get(i).getTitle());
            view.setOnClickListener(new OnPagerClickListener(mContext, mContents.get(i)));
            mViews.add(view);
        }
    }

    //ViewPager点击事件
    private static class OnPagerClickListener implements View.OnClickListener {

        private Context context;
        private NewsSummary summary;

        public OnPagerClickListener(Context context, NewsSummary summary) {
            this.context = context;
            this.summary = summary;
        }

        @Override
        public void onClick(View v) {
            if (context != null && summary != null) {
                if (((MainActivity) context).isNetworkConnected()) {
                    Intent intent = new Intent(context, NewsContentActivity.class);
                    intent.putExtra("id", summary.getId());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void startTimingPageRoll() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new PagerTimerTask(this);
            timer.schedule(timerTask, 5000, 5000);
        }
    }

    public void stopTimingPageRoll() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public void resetTimingPageRoll() {
        stopTimingPageRoll();
        startTimingPageRoll();
    }

    //定时滚动任务
    private static class PagerTimerTask extends TimerTask {

        private WeakReference<TopNewsSummaryPagerAdapter> ref;

        public PagerTimerTask(TopNewsSummaryPagerAdapter adapter) {
            this.ref = new WeakReference<TopNewsSummaryPagerAdapter>(adapter);
        }

        @Override
        public void run() {
            TopNewsSummaryPagerAdapter adapter = ref.get();
            if (ref.get() != null) {
                ((Activity) adapter.mContext).runOnUiThread(() -> {
                    if (adapter.mContents.size() > 0 && !adapter.isLoading()) {
//                            Toast.makeText(mContext, "测试", Toast.LENGTH_SHORT).show();
                        //????????此时子线程执行update()，变量mContent改为0，会产生错误，但几率很小
                        adapter.vp.setCurrentItem((adapter.vp.getCurrentItem() + 1) % adapter.mContents.size());
                    }
                });
            }
        }
    }
}
