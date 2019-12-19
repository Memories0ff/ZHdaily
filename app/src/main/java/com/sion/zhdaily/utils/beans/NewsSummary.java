package com.sion.zhdaily.utils.beans;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class NewsSummary implements Serializable {
    //新闻ID
    private int id;
    //标题
    private String title;
    //内容URL
    private String newsContentUrl;
    //提示内容
    private String hint;
    //标题图片URL
    private String imageUrl;
    //日期“xx月xx日 星期x”
    private String dateStr;
    //是这一天中加载的第一条新闻
    private boolean isFirstNewsSummary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsContentUrl() {
        return newsContentUrl;
    }

    public void setNewsContentUrl(String newsContentUrl) {
        this.newsContentUrl = newsContentUrl;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public boolean isFirstNewsSummary() {
        return isFirstNewsSummary;
    }

    public void setFirstNewsSummary(boolean firstNewsSummary) {
        isFirstNewsSummary = firstNewsSummary;
    }

    public NewsSummary(JSONObject object, boolean isTop) {
        try {
            this.id = object.getInt("id");
            this.title = object.getString("title");
            this.newsContentUrl = object.getString("url");
            this.hint = object.getString("hint");
            this.imageUrl = isTop ? (object.getString("image")) : (object.getJSONArray("images").getString(0));
            this.isFirstNewsSummary = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("first:%s | date:%s | id:%d | title:%s | newsContentUrl:%s | hint:%s | imageUrl:%s", isFirstNewsSummary, dateStr, this.id, this.title, this.newsContentUrl, this.hint, this.imageUrl);
    }
}
