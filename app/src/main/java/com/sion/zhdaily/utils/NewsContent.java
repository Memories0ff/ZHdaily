package com.sion.zhdaily.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsContent {
    public String title;
    public String htmlContent;
    public String imageUrl;
    public String cssUrl;
    public int popularity;
    public int longComments;
    public int shortComments;
    public int comments;

    public NewsContent(String json, String extraInfoJson) {
        try {
            JSONObject object = new JSONObject(json);
            this.title = object.getString("title");
            this.htmlContent = object.getString("body");
            this.imageUrl = object.getString("image");
            this.cssUrl = object.getJSONArray("css").getString(0);
            JSONObject extraObject = new JSONObject(extraInfoJson);
            this.popularity = extraObject.getInt("popularity");
            this.longComments = extraObject.getInt("long_comments");
            this.shortComments = extraObject.getInt("short_comments");
            this.comments = extraObject.getInt("comments");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
