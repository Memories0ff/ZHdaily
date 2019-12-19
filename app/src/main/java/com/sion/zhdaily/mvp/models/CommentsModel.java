package com.sion.zhdaily.mvp.models;

import android.app.Activity;

import com.sion.zhdaily.helpers.CommentHelper;
import com.sion.zhdaily.helpers.DBHelper;
import com.sion.zhdaily.tools.base.BaseModel;
import com.sion.zhdaily.utils.beans.Comment;

import java.util.List;

public class CommentsModel extends BaseModel {

    private CommentHelper commentHelper = null;
    private DBHelper dbHelper = null;

//    private int newsId;
//    private int longCommentsNum;
//    private int shortCommentsNum;
//    private Activity activity;


    public CommentsModel(int newsId, int longCommentsNum, int shortCommentsNum, Activity activity) {
        commentHelper = new CommentHelper(newsId, longCommentsNum, shortCommentsNum, activity);
        dbHelper = new DBHelper(activity);
    }

//    private void initCommentHelper(int newsId, int longCommentsNum, int shortCommentsNum, Activity activity) {
//        if (commentHelper != null) {
//            commentHelper = new CommentHelper(newsId, longCommentsNum, shortCommentsNum, activity);
//        }
//    }
//
//    private void initDBHelper(Activity activity) {
//        if (dbHelper != null) {
//            dbHelper = new DBHelper(activity);
//        }
//    }

    //------------------------------------评论加载操作------------------------------------

    //获取评论加载帮助类
    public CommentHelper getCommentHelper() {
        return commentHelper;
    }

    //获取当前已加载短评数
    public int getCurrentLoadedShortComments() {
        return commentHelper.getCurrentLoadedShortComments();
    }

    //获取长评数据源
    public List<Comment> getLongComments() {
        return commentHelper.getLongComments();
    }

    //获取短评数据源
    public List<Comment> getShortComments() {
        return commentHelper.getShortComments();
    }

    //获取所有长评
    public void obtainAllLongComments() {
        commentHelper.obtainAllLongComments();
    }

    //单次加载最多20条评论
    public void obtainShortCommentsByStep() {
        commentHelper.obtainShortCommentsByStep();
    }

    //清除所有短评
    public void clearAllShortComments() {
        commentHelper.clearAllShortComments();
    }

    //------------------------------------数据库操作------------------------------------

    //添加评论点赞记录
    public void insertCommentLikeRecord(int authorId, long commentTime) {
        dbHelper.insertCommentLikeRecord(authorId, commentTime);
    }

    //删除评论点赞记录
    public void deleteCommentLikeRecord(int authorId, long commentTime) {
        dbHelper.deleteCommentLikeRecord(authorId, commentTime);
    }

    //查找是否存在点赞记录
    public boolean findCommentLikeRecord(int authorId, long commentTime) {
        return dbHelper.findCommentLikeRecord(authorId, commentTime);
    }

}
