package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsContent;
import com.sion.zhdaily.presenters.NewsContentHelper;
import com.sion.zhdaily.views.views.NewsContentNestedScrollView;

public class NewsContentActivity extends Activity {

    //网络状态
    private boolean isNetworkConnected = false;

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    ConnectivityManager connMgr = null;
    NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    //新闻内容处理帮助类
    NewsContentHelper helper = null;
    //新闻内容对象
    NewsContent content = null;

    //toolbar
    Toolbar tbNewsContent = null;
    //评论按钮
    LinearLayout llCommentsBtn = null;
    TextView tvCommentNum = null;
    //点赞按钮
    LinearLayout llThumbBtn = null;
    TextView tvPopularityNum = null;
    //分享按钮
    ImageView ivShareBtn = null;
    //收藏按钮
    ImageView ivCollectBtn = null;

    //appbar
    //标题图片
    ImageView ivNewsContentTitlePic = null;
    //标题文字
    TextView tvNewsContentTitle = null;

    //新闻内容显示
    NewsContentNestedScrollView nsvNewsContent = null;
    WebView wvNewsContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        //网络状态
        connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connMgr.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isNetworkConnected = true;
        }

        tbNewsContent = findViewById(R.id.tb_newsContent);
        tbNewsContent.setNavigationOnClickListener((v) -> finish());

        llCommentsBtn = findViewById(R.id.ll_commentsBtn);
        tvCommentNum = findViewById(R.id.tv_commentsNum);

        llThumbBtn = findViewById(R.id.ll_thumbBtn);
        tvPopularityNum = findViewById(R.id.tv_popularityNum);

        ivShareBtn = findViewById(R.id.iv_newsContentShareBtn);
        ivCollectBtn = findViewById(R.id.iv_newsContentCollectBtn);

        ivNewsContentTitlePic = findViewById(R.id.iv_newsContentTitlePic);
        tvNewsContentTitle = findViewById(R.id.tv_newsContentTitle);

        nsvNewsContent = findViewById(R.id.nsv_newsContent);
        //设置NewsContentNestedScrollView中设置的接口
        final float appbarHeight = getResources().getDimension(R.dimen.news_content_appbar_height);
        nsvNewsContent.setOnTopMovedListener((currentY) -> {
            //随NestedViewScroll中Top的位置确定toolbar的透明度，top越小toolbar越透明，到toolbar高度位置时全透明
            float alpha = (currentY - tbNewsContent.getHeight()) / (appbarHeight - tbNewsContent.getHeight());
            tbNewsContent.setAlpha(alpha);
        });
        nsvNewsContent.setOnScrolledListener((oldY, currentY, dy) -> {
            //防止在不出现Toolbar的情况下慢速上滑到显示图片时，不再显示Toolbar的问题
            if (currentY < 1) {
                tbNewsContent.setAlpha(0);
                tbNewsContent.setVisibility(View.VISIBLE);
            } else {
                //NestedScrollView向上滚动隐藏toolbar，向下滚动则显示
                //此层if-else防抖动
                if (dy < -15) {
                    if (tbNewsContent.getVisibility() != View.VISIBLE) {
                        tbNewsContent.setVisibility(View.VISIBLE);
                        tbNewsContent.setAlpha(1);
                    }
                } else if (dy > 0) {
                    if (tbNewsContent.getVisibility() != View.GONE) {
                        tbNewsContent.setVisibility(View.GONE);
                    }
                }
            }
        });

        wvNewsContent = findViewById(R.id.wv_newsContent);

        //获取从上个activity传来到数据
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        helper = new NewsContentHelper();
        new Thread(() -> {
            //下载新闻内容等数据
            content = helper.getNewsContentById(id);
            runOnUiThread(() -> {
                //显示这些数据
                tvCommentNum.setText("" + content.getComments());
                tvPopularityNum.setText("" + content.getPopularity());

                Glide.with(this).load(content.getImageUrl()).into(ivNewsContentTitlePic);
                tvNewsContentTitle.setText(content.getTitle());

//                webView.getSettings().setJavaScriptEnabled(true);
                wvNewsContent.getSettings().setBlockNetworkImage(false);
                wvNewsContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                wvNewsContent.loadData("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/news_content.css\"" + " /> " + content.getHtmlContent(), "text/html;charset=UTF-8", null);
//                wvNewsContent.loadData(content.getHtmlContent(), "text/html;charset=UTF-8", null);
                wvNewsContent.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_content.css\"" + " /> " + content.getHtmlContent(), "text/html", "UTF-8", null);

                //等到数据加载完后设置点击事件
                llCommentsBtn.setOnClickListener((v) -> {
                    if (isNetworkConnected()) {
                        Intent commentsIntent = new Intent(NewsContentActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("newsId", content.getId());
                        commentsIntent.putExtra("longCommentNum", content.getLongComments());
                        commentsIntent.putExtra("shortCommentNum", content.getShortComments());
                        commentsIntent.putExtra("commentNum", content.getLongComments() + content.getShortComments());
                        startActivity(commentsIntent);
                    } else {
                        Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
                    }
                });
                llThumbBtn.setOnClickListener((v) -> Toast.makeText(this, "点赞", Toast.LENGTH_SHORT).show());
                ivShareBtn.setOnClickListener((v) -> Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show());
                ivCollectBtn.setOnClickListener((v) -> Toast.makeText(this, "收藏", Toast.LENGTH_SHORT).show());

            });
        }).start();
    }

    @Override
    protected void onResume() {
        nsvNewsContent.startUiChangeThread();
        super.onResume();
    }

    @Override
    protected void onPause() {
        nsvNewsContent.stopUiChangeThread();
        super.onPause();
    }

    class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
//            Toast.makeText(NewsContentActivity.this, "网络已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
//            Toast.makeText(NewsContentActivity.this, "网络已断开", Toast.LENGTH_SHORT).show();
        }
    }

}
