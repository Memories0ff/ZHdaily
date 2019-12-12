package com.sion.zhdaily.models.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class NewsContent implements Serializable {
    //id
    private int id;
    //标题
    private String title;
    //HTML新闻内容
    private String htmlContent;
    //图片URL
    private String imageUrl;
    //CSS URL
    private String cssUrl;
    //点赞数
    private int popularity;
    //长评数
    private int longComments;
    //短评数
    private int shortComments;
    //总评数
    private int comments;

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

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    public void setCssUrl(String cssUrl) {
        this.cssUrl = cssUrl;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getLongComments() {
        return longComments;
    }

    public void setLongComments(int longComments) {
        this.longComments = longComments;
    }

    public int getShortComments() {
        return shortComments;
    }

    public void setShortComments(int shortComments) {
        this.shortComments = shortComments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public NewsContent(String json, String extraInfoJson) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getInt("id");
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

    public NewsContent(int id, String title, String htmlContent, String imageUrl, String cssUrl, int popularity, int longComments, int shortComments, int comments) {
        this.id = id;
        this.title = title;
        this.htmlContent = htmlContent;
        this.imageUrl = imageUrl;
        this.cssUrl = cssUrl;
        this.popularity = popularity;
        this.longComments = longComments;
        this.shortComments = shortComments;
        this.comments = comments;
    }
}
