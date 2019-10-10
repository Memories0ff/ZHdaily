package com.sion.zhdaily.models.beans;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsSummary {
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

    public NewsSummary(String json, boolean isTop) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getInt("id");
            this.title = object.getString("title");
            this.newsContentUrl = object.getString("url");
            this.hint = object.getString("hint");
            this.imageUrl = isTop ? (object.getString("image")) : (object.getJSONArray("images").getString(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("id:%d | title:%s | newsContentUrl:%s | hint:%s | imageUrl:%s", this.id, this.title, this.newsContentUrl, this.hint, this.imageUrl);
    }
}
