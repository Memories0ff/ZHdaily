package com.sion.zhdaily.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsSummary;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TopNewsSummaryPagerAdapter extends PagerAdapter {

    Context mContext = null;
    List<NewsSummary> mContents = null;
    ViewPager vp = null;

    private static Handler handler = new Handler();

    //是否正在加载内容
    boolean isLoading = false;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_top_view_pager_item_view, container, false);
        ImageView imageView = view.findViewById(R.id.iv_topNewsPic);
        Glide.with(mContext).load(mContents.get(position).getImageUrl()).into(imageView);
        TextView textView = view.findViewById(R.id.tv_topNewsTitle);
        textView.setText(mContents.get(position).getTitle());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mContents.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        container.addView(view);
        return view;
        //????????????????????√
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //????????????????????√
//        container.removeView(mViewList.get(position));
        container.removeView((View) object);
    }

    public void startTimingPageRoll() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(() -> {
                    if (mContents.size() > 0)
                        vp.setCurrentItem((vp.getCurrentItem() + 1) % mContents.size());
                });
            }
        };
        timer.schedule(timerTask, 5000, 5000);

    }
}
