package com.sion.zhdaily.models.beans;

import org.json.JSONException;
import org.json.JSONObject;

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
    //所回复消息内容
    private String replyContent;
    //所回复消息状态
    private int replyStatus = 0;
    //所回复消息作者id
    private int replyAuthorId;
    //所回复消息作者
    private String replyAuthor;
    //错误代码
    private int errMsg;

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

    public int getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(int errMsg) {
        this.errMsg = errMsg;
    }

    public Comment(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.author = object.getString("author");
            this.content = object.getString("content");
            this.avatarUrl = object.getString("avatar");
            this.id = object.getInt("id");
            this.likes = object.getInt("likes");
            this.time = object.getLong("time");

            if (object.has("reply_to")) {
                JSONObject replyObject = object.getJSONObject("reply_to");
                this.replyContent = replyObject.getString("content");
                this.replyStatus = replyObject.getInt("status");
                this.replyAuthorId = replyObject.getInt("id");
                this.replyAuthor = replyObject.getString("author");
                if (replyStatus != 0) {
                    this.errMsg = replyObject.getInt("err_msg");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
