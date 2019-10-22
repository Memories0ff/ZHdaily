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

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsContent;
import com.sion.zhdaily.presenters.NewsContentHelper;
import com.sion.zhdaily.views.views.NewsContentNestedScrollView;

public class NewsContentActivity extends Activity {

    NewsContentHelper helper = null;
    NewsContent content = null;

    Toolbar tbNewsContent = null;

    LinearLayout llCommentsBtn = null;
    TextView tvCommentNum = null;

    LinearLayout llThumbBtn = null;
    TextView tvPopularityNum = null;

    CoordinatorLayout clNewsContent = null;
    ImageView ivNewsContentTitlePic = null;
    TextView tvNewsContentTitle = null;

    NewsContentNestedScrollView nsvNewsContent = null;
    WebView wvNewsContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        tbNewsContent = findViewById(R.id.tb_newsContent);
        tbNewsContent.setNavigationOnClickListener((v) -> Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show());

        llCommentsBtn = findViewById(R.id.ll_commentsBtn);
        tvCommentNum = findViewById(R.id.tv_commentsNum);

        llThumbBtn = findViewById(R.id.ll_thumbBtn);
        tvPopularityNum = findViewById(R.id.tv_popularityNum);

        clNewsContent = findViewById(R.id.cl_newsContent);
        ivNewsContentTitlePic = findViewById(R.id.iv_newsContentTitlePic);
        tvNewsContentTitle = findViewById(R.id.tv_newsContentTitle);

        nsvNewsContent = findViewById(R.id.nsv_newsContent);
        //
        nsvNewsContent.setOnNewsContentNestedScrollViewTopMovedListener((currentY, transRange) -> {
            float alpha = (currentY - tbNewsContent.getHeight()) / transRange;
            tbNewsContent.setAlpha(alpha);
        });
        nsvNewsContent.setOnNewsContentNestedScrollSlidedListener((oldY, currentY, dy) -> {
            //防抖动
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

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        helper = new NewsContentHelper();
        new Thread(() -> {
            content = helper.getNewsContentById(id);
            runOnUiThread(() -> {
                llCommentsBtn.setOnClickListener((v) -> Toast.makeText(this, "评论", Toast.LENGTH_SHORT).show());
                tvCommentNum.setText("" + content.getComments());

                llThumbBtn.setOnClickListener((v) -> Toast.makeText(this, "点赞", Toast.LENGTH_SHORT).show());
                tvPopularityNum.setText("" + content.getPopularity());

                Glide.with(this).load(content.getImageUrl()).into(ivNewsContentTitlePic);
                tvNewsContentTitle.setText(content.getTitle());

//                webView.getSettings().setJavaScriptEnabled(true);
                wvNewsContent.getSettings().setBlockNetworkImage(false);
                wvNewsContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                wvNewsContent.loadData("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/news_content.css\"" + " /> " + content.getHtmlContent(), "text/html;charset=UTF-8", null);
//                wvNewsContent.loadData(content.getHtmlContent(), "text/html;charset=UTF-8", null);
                wvNewsContent.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_content.css\"" + " /> " + content.getHtmlContent(), "text/html", "UTF-8", null);
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
