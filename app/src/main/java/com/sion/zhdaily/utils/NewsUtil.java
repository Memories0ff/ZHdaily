package com.sion.zhdaily.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsUtil {

    //获取多个最新及头条新闻Json
    public static String getLatestNewsSummariesJson() {
        Request.Builder builder = new Request.Builder();
        builder.url("http://118.89.204.190/api/4/stories/latest")
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
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

    //获取以前某天前一天的多个新闻json
    public static String getOldNewsSummariesJson(String date) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://118.89.204.190/api/4/stories/before/" + date)
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
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

    //从Json获取多个新闻简介
    public static List<NewsSummary> getNewsSummaries(String newsJson) {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(newsJson).getJSONArray("stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), false));
            }
            return newsSummaries;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //从Json获取所有头条新闻简介
    public static List<NewsSummary> getTopNewsSummaries(String latestNewsJson) {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(latestNewsJson).getJSONArray("top_stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), true));
            }
            return newsSummaries;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取新闻内容Json
    public static String getNewsContentJson(int id) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://118.89.204.190/api/4/story/" + id)
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
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

    //获取新闻额外信息（点赞评论数等）信息
    public static String getNewsContentExtraJson(int id) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://118.89.204.190/api/4/story-extra/" + id)
                //添加Header防400 Bad Request
                .addHeader("Host", "news-at.zhihu.com")
                .addHeader("User-Agent", "DailyApi/4 (Linux; Android 5.1.1; OPPO R11 Build/OPPO /R11/R11/NMF26X/zh_CN) Google-HTTP-Java-Client/1.22.0 (gzip) Google-HTTP-Java-Client/1.22.0 (gzip)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache");
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
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
