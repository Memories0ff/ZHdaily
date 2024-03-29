package com.sion.zhdaily.helpers;

import android.content.Context;

import com.sion.zhdaily.utils.CommentUtil;
import com.sion.zhdaily.utils.beans.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentHelper {

    Context context;

    public int allNumOfLongComments = 0;
    public int allNumOfShortComments = 0;

    //数据源
    private List<Comment> longComments = new ArrayList<>();
    private List<Comment> shortComments = new ArrayList<>();

    public List<Comment> getLongComments() {
        return longComments;
    }

    public List<Comment> getShortComments() {
        return shortComments;
    }

    //新闻ID
    public int newsId = 0;

    //当前加载短评数
    private int currentLoadedShortComments = 0;

    public int getCurrentLoadedShortComments() {
        return currentLoadedShortComments;
    }

    //短评加载过程是否完成
    private boolean isShortCommentsLoadOver = false;

    public boolean isShortCommentsLoadOver() {
        return isShortCommentsLoadOver;
    }

    public CommentHelper(int newsId, int allNumOfLongComments, int allNumOfShortComments, Context context) {
        this.newsId = newsId;
        this.allNumOfLongComments = allNumOfLongComments;
        this.allNumOfShortComments = allNumOfShortComments;
        this.context = context;
    }

    //获取所有长评
    public void obtainAllLongComments() {
        longComments.clear();
        longComments.addAll(CommentUtil.getAllLongComments(newsId, context));
    }

    //获取所有短评
    public void obtainAllShortComments() {
        shortComments.clear();
        shortComments.addAll(CommentUtil.getAllShortComments(newsId, context));
    }

    //单次加载最多20条短评
    public void obtainShortCommentsByStep() {
        if (isShortCommentsLoadOver) {
            return;
        }
        List<Comment> comments = CommentUtil.getShortCommentByStep(newsId, shortComments.size() == 0 ? -1 : (shortComments.get(shortComments.size() - 1).getId()), context);
        currentLoadedShortComments = comments.size();
        if (comments.size() < 20) {
            isShortCommentsLoadOver = true;
        }
        shortComments.addAll(comments);
    }

    //清除所有短评
    public void clearAllShortComments() {
        shortComments.clear();
        currentLoadedShortComments = 0;
        isShortCommentsLoadOver = false;
    }
}
