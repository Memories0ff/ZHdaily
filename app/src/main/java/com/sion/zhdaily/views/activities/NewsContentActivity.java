package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.content.Intent;
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
        nsvNewsContent.setOnTopMovedListener((currentY, transRange) -> {
            //随NestedViewScroll中Top的位置确定toolbar的透明度，top越小toolbar越透明，到toolbar高度到位置时全透明
            float alpha = (currentY - tbNewsContent.getHeight()) / transRange;
            tbNewsContent.setAlpha(alpha);
        });
        nsvNewsContent.setOnScrolledListener((oldY, currentY, dy) -> {
            //NestedScrollView向上滚动隐藏toolbar，向下滚动则显示
            //最外if-else防抖动
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
                    Intent commentsIntent = new Intent(NewsContentActivity.this, CommentsActivity.class);
                    commentsIntent.putExtra("id", content.getId());
                    startActivity(commentsIntent);
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

}
