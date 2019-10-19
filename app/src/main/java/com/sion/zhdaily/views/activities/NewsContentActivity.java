package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class NewsContentActivity extends Activity {

    NewsContentHelper helper = null;
    NewsContent content = null;

    Toolbar tb = null;

    LinearLayout llCommentsBtn = null;
    TextView tvCommentNum = null;

    LinearLayout llThumbBtn = null;
    TextView tvPopularityNum = null;

    ImageView ivNewsContentTitlePic = null;
    TextView tvNewsContentTitle = null;

    WebView wvNewsContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        tb = findViewById(R.id.tb_newsContent);
        tb.setNavigationOnClickListener((v) -> Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show());

        llCommentsBtn = findViewById(R.id.ll_commentsBtn);
        tvCommentNum = findViewById(R.id.tv_commentsNum);

        llThumbBtn = findViewById(R.id.ll_thumbBtn);
        tvPopularityNum = findViewById(R.id.tv_popularityNum);

        ivNewsContentTitlePic = findViewById(R.id.iv_newsContentTitlePic);
        tvNewsContentTitle = findViewById(R.id.tv_newsContentTitle);

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
}
