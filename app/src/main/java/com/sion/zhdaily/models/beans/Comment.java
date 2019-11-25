package com.sion.zhdaily.models.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    //作者
    private String author;
    //内容
    private String content;
    //头像链接
    private String avatarUrl;
    //评论作者id
    private int id;
    //获赞数量
    private int likes;
    //评论时间
    private long time;
    private String timeStr;
    //所回复消息内容
    private String replyContent;
    //所回复消息状态
    private int replyStatus;
    //所回复消息作者id
    private int replyAuthorId;
    //所回复消息作者
    private String replyAuthor;
    //错误代码
    private String errMsg;
    //是否点赞
    private boolean isLiked;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public int getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(int replyStatus) {
        this.replyStatus = replyStatus;
    }

    public int getReplyAuthorId() {
        return replyAuthorId;
    }

    public void setReplyAuthorId(int replyAuthorId) {
        this.replyAuthorId = replyAuthorId;
    }

    public String getReplyAuthor() {
        return replyAuthor;
    }

    public void setReplyAuthor(String replyAuthor) {
        this.replyAuthor = replyAuthor;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public Comment(JSONObject object) {
        try {
            this.author = object.getString("author");
            this.content = object.getString("content");
            this.avatarUrl = object.getString("avatar");
            this.id = object.getInt("id");
            this.likes = object.getInt("likes");
            this.time = object.getLong("time");
            this.timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(this.time * 1000));

            if (object.has("reply_to")) {
                JSONObject replyObject = object.getJSONObject("reply_to");
                this.replyStatus = replyObject.getInt("status");
                if (replyStatus == 0) {
                    this.replyContent = replyObject.getString("content");
                    this.replyAuthorId = replyObject.getInt("id");
                    this.replyAuthor = replyObject.getString("author");
                } else {
                    this.errMsg = replyObject.getString("error_msg");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
