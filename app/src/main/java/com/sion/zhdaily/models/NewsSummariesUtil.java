package com.sion.zhdaily.models;

import android.util.Log;

import com.sion.zhdaily.models.beans.NewsSummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsSummariesUtil {

    //最新和头条新闻共用的Json。最新和头条新闻是分两个方法加载的，为防止在两个方法中都下载一次相同的Json，将Json保存在此变量中
    private static String todayNewsSummariesJson = null;

    //获取多个最新及头条新闻Json
    public static String getTodayNewsSummariesJson() {
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.zhihu.com/api/4/stories/latest")
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

    //更新最新及头条新闻共用的Json（更新操作时要用，“获取所有最新和头条新闻简介”这两个方法无法更新最新和头条新闻共用的Json）
    public static void updateTodayNewsSummariesJson() {
        todayNewsSummariesJson = getTodayNewsSummariesJson();
    }

    //获取以前某天前一天的多个新闻json
    public static String getOldNewsSummariesJson(String date) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.zhihu.com/api/4/stories/before/" + date)
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

    //获取所有最新新闻简介
    public static List<NewsSummary> getLatestNewsSummaries() {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        //最新及头条新闻Json为空则加载
        if (todayNewsSummariesJson == null) {
            todayNewsSummariesJson = getTodayNewsSummariesJson();
        }
        //没有获取到Json，返回没有元素的集合
        if (todayNewsSummariesJson == null) {
            return newsSummaries;
        }
        try {
            JSONArray array = new JSONObject(todayNewsSummariesJson).getJSONArray("stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsSummaries;
    }

    //获取所有头条新闻简介
    public static List<NewsSummary> getTopNewsSummaries() {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        //最新及头条新闻Json为空则加载
        if (todayNewsSummariesJson == null) {
            todayNewsSummariesJson = getTodayNewsSummariesJson();
        }
        //没有获取到Json，返回没有元素的集合
        if (todayNewsSummariesJson == null) {
            return newsSummaries;
        }
        try {
            JSONArray array = new JSONObject(todayNewsSummariesJson).getJSONArray("top_stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), true));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsSummaries;
    }

    //获取date前一天新闻简介
    public static List<NewsSummary> getOldNewsSummaries(String date) {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        String oldNewsSummariesJson = getOldNewsSummariesJson(date);
        //没有获取到Json，返回没有元素的集合
        if (oldNewsSummariesJson == null) {
            return newsSummaries;
        }
        try {
            JSONArray array = new JSONObject(oldNewsSummariesJson).getJSONArray("stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsSummaries;
    }
}
