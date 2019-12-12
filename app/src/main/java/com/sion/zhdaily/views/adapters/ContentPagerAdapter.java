package com.sion.zhdaily.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sion.zhdaily.views.activities.NewsContentActivity;
import com.sion.zhdaily.views.fragments.ContentFragment;

public class ContentPagerAdapter extends FragmentStatePagerAdapter {

    private int index;
    private int newsNum;
    private ViewPager vp;
    private NewsContentActivity mActivity;

    public ContentPagerAdapter(@NonNull FragmentManager fm, NewsContentActivity mActivity, ViewPager vp, int index, int newsNum) {
        super(fm);
        this.mActivity = mActivity;
        this.vp = vp;
        this.index = index;
        this.newsNum = newsNum;
        vp.setOffscreenPageLimit(1);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActivity.getPresenter().toNews(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ContentFragment();
    }

    @Override
    public int getCount() {
        return newsNum;
    }


}
