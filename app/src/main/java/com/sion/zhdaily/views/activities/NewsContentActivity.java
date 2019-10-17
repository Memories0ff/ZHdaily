package com.sion.zhdaily.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsContent;
import com.sion.zhdaily.presenters.NewsContentHelper;

public class NewsContentActivity extends Activity {

    NewsContentHelper helper = null;
    NewsContent content = null;

    WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        webView = findViewById(R.id.webview);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        helper = new NewsContentHelper();
        new Thread(() -> {
            content = helper.getNewsContentById(id);
            runOnUiThread(() -> {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBlockNetworkImage(false);
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webView.loadData("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + content.getCssUrl() + "\" /> " + content.getHtmlContent(), "text/html;charset=UTF-8", null);
//                webView.loadDataWithBaseURL("about:blank", "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + content.getCssUrl() + "\" /> " + content.getHtmlContent(), "text/html", "UTF-8", null);
            });
        }).start();

    }
}
