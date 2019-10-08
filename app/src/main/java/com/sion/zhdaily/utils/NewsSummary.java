package com.sion.zhdaily.utils;


import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsSummary {
    public int id;
    public String title;
    public String newsContentUrl;
    public String hint;
    public String imageUrl;

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
