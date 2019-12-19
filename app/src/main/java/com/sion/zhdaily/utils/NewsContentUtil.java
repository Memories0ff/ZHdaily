package com.sion.zhdaily.utils;

import android.util.Log;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsContentUtil {
    //获取新闻内容Json
    public static String getNewsContentJson(int id) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.zhihu.com/api/4/story/" + id)
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder().hostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                //强行返回true 即验证成功
                return true;
            }
        }).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                Log.e("Json", "连接失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取新闻额外信息（点赞评论数等）信息Json
    public static String getNewsContentExtraJson(int id) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.zhihu.com/api/4/story-extra/" + id)
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder().hostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                //强行返回true 即验证成功
                return true;
            }
        }).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                Log.e("Json", "连接失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
