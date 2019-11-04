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
//        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_top_view_pager_item_view, container, false);
//        ImageView imageView = view.findViewById(R.id.iv_topNewsPic);
//        Glide.with(mContext)
//                .load(mContents.get(position).getImageUrl())
//                .skipMemoryCache(false)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageView);
//        TextView textView = view.findViewById(R.id.tv_topNewsTitle);
//        textView.setText(mContents.get(position).getTitle());
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(mContext, mContents.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                if (((MainActivity) mContext).isNetworkConnected()) {
//                    Intent intent = new Intent(mContext, NewsContentActivity.class);
//                    intent.putExtra("id", mContents.get(position).getId());
//                    mContext.startActivity(intent);
//                } else {
//                    Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        container.addView(view);
//        return view;
        //????????????????????√
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
            Glide.with(mContext)
                    .load(mContents.get(i).getImageUrl())
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            TextView textView = view.findViewById(R.id.tv_topNewsTitle);
            textView.setText(mContents.get(i).getTitle());
            final int _i = i;
            view.setOnClickListener((v) -> {
//                Toast.makeText(mContext, mContents.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                if (((MainActivity) mContext).isNetworkConnected()) {
                    Intent intent = new Intent(mContext, NewsContentActivity.class);
                    intent.putExtra("id", mContents.get(_i).getId());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            });
            mViews.add(view);
        }
    }


    public void startTimingPageRoll() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    ((Activity) mContext).runOnUiThread(() -> {
                        if (mContents.size() > 0 && !isLoading()) {
//                            Toast.makeText(mContext, "测试", Toast.LENGTH_SHORT).show();
                            //????????此时子线程执行update()，变量mContent改为0，会产生错误，但几率很小
                            vp.setCurrentItem((vp.getCurrentItem() + 1) % mContents.size());
                        }
                    });
                }
            };
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
}
