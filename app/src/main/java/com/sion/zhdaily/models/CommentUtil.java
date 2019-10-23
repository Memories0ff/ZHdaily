package com.sion.zhdaily.models;

import android.util.Log;

import com.sion.zhdaily.models.beans.Comment;

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

public class CommentUtil {
    private static String getLongCommentsJson(int newsId) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://news-at.zhihu.com/api/4/story/" + newsId + "/long-comments")
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

    private static String getShortCommentsJson(int newsId) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://news-at.zhihu.com/api/4/story/" + newsId + "/short-comments")
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

    public static List<Comment> getAllLongComments(int newsId) {

        List<Comment> longComments = new ArrayList<>();
        String longCommentsJson = getLongCommentsJson(newsId);
        //没有获取到Json，返回没有元素的集合
        if (longCommentsJson == null) {
            return longComments;
        }
        try {
            JSONArray array = new JSONObject(longCommentsJson).getJSONArray("comments");
            for (int i = 0; i < array.length(); i++) {
                longComments.add(new Comment(array.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return longComments;
    }

    public static List<Comment> getAllShortComments(int newsId) {

        List<Comment> shortComments = new ArrayList<>();
        String longCommentsJson = getShortCommentsJson(newsId);
        //没有获取到Json，返回没有元素的集合
        if (longCommentsJson == null) {
            return shortComments;
        }
        try {
            JSONArray array = new JSONObject(longCommentsJson).getJSONArray("comments");
            for (int i = 0; i < array.length(); i++) {
                shortComments.add(new Comment(array.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shortComments;
    }
}
