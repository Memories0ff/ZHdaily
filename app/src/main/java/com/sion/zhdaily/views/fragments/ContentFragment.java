package com.sion.zhdaily.views.fragments;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sion.zhdaily.R;
import com.sion.zhdaily.mvp.presenters.ContentFragmentPresenter;
import com.sion.zhdaily.mvp.views.IContentFragmentView;
import com.sion.zhdaily.tools.base.BaseFragment;
import com.sion.zhdaily.views.activities.NewsContentActivity;
import com.sion.zhdaily.views.views.NewsContentNestedScrollView;

public class ContentFragment extends BaseFragment<IContentFragmentView, ContentFragmentPresenter> implements IContentFragmentView {

    //appbar
    //标题图片
    ImageView ivNewsContentTitlePic = null;
    //标题文字
    TextView tvNewsContentTitle = null;

    //新闻内容显示
    NewsContentNestedScrollView nsvNewsContent = null;
    WebView wvNewsContent = null;

    //显示新闻内容最外层UI CoordinatorLayout
    CoordinatorLayout clNewsContent;
    //正在加载提示
    TextView tvLoadingHint;

    @Override
    protected IContentFragmentView createView() {
        return this;
    }

    @Override
    protected ContentFragmentPresenter createPresenter() {
        return new ContentFragmentPresenter((NewsContentActivity) getActivity());
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_news_content;
    }

    @Override
    protected void initView(View rootView) {

        clNewsContent = rootView.findViewById(R.id.cl_newsContent);
        tvLoadingHint = rootView.findViewById(R.id.tv_loadingHint);

        ivNewsContentTitlePic = rootView.findViewById(R.id.iv_newsContentTitlePic);
        tvNewsContentTitle = rootView.findViewById(R.id.tv_newsContentTitle);

        nsvNewsContent = rootView.findViewById(R.id.nsv_newsContent);

        //设置控件
        //设置NewsContentNestedScrollView中设置的接口
        final float appbarHeight = getResources().getDimension(R.dimen.news_content_appbar_height);
        nsvNewsContent.setOnTopMovedListener((currentY) -> {
            //随NestedViewScroll中Top的位置确定toolbar的透明度，top越小toolbar越透明，到toolbar高度位置时全透明
            if (currentY > 0) {
                NewsContentActivity newsContentActivity = (NewsContentActivity) ContentFragment.this.getActivity();
                float alpha = (currentY - newsContentActivity.tbNewsContent.getHeight()) / (appbarHeight - newsContentActivity.tbNewsContent.getHeight());
                newsContentActivity.tbNewsContent.setAlpha(alpha < 0 ? 0 : alpha);
            }
        });
        nsvNewsContent.setOnScrolledListener((oldY, currentY, dy) -> {
            NewsContentActivity newsContentActivity = (NewsContentActivity) ContentFragment.this.getActivity();
            //防止在不出现Toolbar的情况下慢速上滑到显示图片时，不再显示Toolbar的问题
            if (currentY < 1) {
                newsContentActivity.tbNewsContent.setAlpha(0);
                newsContentActivity.tbNewsContent.setVisibility(View.VISIBLE);
            } else {
                //NestedScrollView向上滚动隐藏toolbar，向下滚动则显示
                //此层if-else防抖动
                if (dy < -15) {
//                    if (newsContentActivity.tbNewsContent.getVisibility() != View.VISIBLE) {
                    newsContentActivity.tbNewsContent.setVisibility(View.VISIBLE);
                    newsContentActivity.tbNewsContent.setAlpha(1);
//                    }
                } else if (dy > 0) {
//                    if (newsContentActivity.tbNewsContent.getVisibility() != View.GONE) {
                    newsContentActivity.tbNewsContent.setVisibility(View.GONE);
//                    }
                }
            }
        });

        wvNewsContent = new WebView(getActivity().getApplicationContext());
        nsvNewsContent.addView(wvNewsContent);
    }

    @Override
    public void onFragmentLoad() {
        getPresenter().loadContent();
    }

    @Override
    public void onFragmentLoadStop() {

    }

    @Override
    public void onResume() {
//        nsvNewsContent.startUiChangeThread();
        super.onResume();
    }

    @Override
    public void onPause() {
//        nsvNewsContent.stopUiChangeThread();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        releaseWebView();
        super.onDestroy();
    }

    //Activity销毁前释放WebView
    private void releaseWebView() {
        if (wvNewsContent != null) {
            wvNewsContent.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvNewsContent.getSettings().setJavaScriptEnabled(false);
            wvNewsContent.clearHistory();
            wvNewsContent.clearCache(true);
            wvNewsContent.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            wvNewsContent.freeMemory();
            wvNewsContent.pauseTimers();
            wvNewsContent.clearView();
            wvNewsContent.removeAllViews();
            wvNewsContent.destroy();
            wvNewsContent = null;
        }
    }

    //获取新闻数据后更新界面
    @Override
    public void updateUIAfterLoadingFinish() {
        NewsContentActivity newsContentActivity = (NewsContentActivity) ContentFragment.this.getActivity();
        //防止在执行时Activity已经退出造成崩溃
        try {
            //显示这些数据
            newsContentActivity.tvCommentNum.setText("" + getPresenter().getCurrentContent().getComments());
            //数据库操作
            //查询到点赞记录则设为已点赞的状态
            if (newsContentActivity.getPresenter().findNewsLikeRecord()) {
                newsContentActivity.getPresenter().setLiked(true);
                newsContentActivity.ivThumb.setImageResource(R.mipmap.thumb_up_orange);
                newsContentActivity.tvPopularityNum.setText("" + (getPresenter().getCurrentContent().getPopularity() + 1));
            } else {
                newsContentActivity.tvPopularityNum.setText("" + getPresenter().getCurrentContent().getPopularity());
            }
            //异步加载，使用Glide加载前要判断挂靠的Activity是否已经销毁
            if (!newsContentActivity.isDestroyed()) {
                Glide.with(newsContentActivity)
                    .load(getPresenter().getCurrentContent().getImageUrl())
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(ivNewsContentTitlePic);
            } else {
                //若已销毁则直接退出
                return;
            }
            tvNewsContentTitle.setText(getPresenter().getCurrentContent().getTitle());

            //若在此之前Activity已被销毁则不执行
            if (wvNewsContent != null) {
//                webView.getSettings().setJavaScriptEnabled(true);
                wvNewsContent.getSettings().setBlockNetworkImage(false);
                wvNewsContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                wvNewsContent.loadData("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + content.getCssUrl() + "\" /> " + content.getHtmlContent(), "text/html;charset=UTF-8", null);
//                wvNewsContent.loadData(content.getHtmlContent(), "text/html;charset=UTF-8", null);
                wvNewsContent.loadDataWithBaseURL("file:///android_asset/",
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_content.css\"" + " /> " + getPresenter().getCurrentContent().getHtmlContent(),
                    "text/html", "UTF-8", null);

                //去掉提示，显示新闻内容
                clNewsContent.setVisibility(View.VISIBLE);
                tvLoadingHint.setVisibility(View.GONE);
            } else {
                //若已销毁则直接退出
                return;
            }
        } catch (Exception e) {
            Log.e("NewsContentActivity Unknown ERROR", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadingFailedHint(String str) {
        tvLoadingHint.setText(str);
    }

}
