package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.CommentUtil;
import com.sion.zhdaily.models.beans.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentHelper {
    public int allNumOfLongComments = 0;
    public int allNumOfShortComments = 0;

    public List<Comment> longComments = new ArrayList<>();
    public List<Comment> shortComments = new ArrayList<>();

    //新闻ID
    public int newsId = 0;

    //当前加载短评数
    public int currentLoadedShortComments = 0;

    //短评加载过程是否完成
    private boolean isShortCommentsLoadOver = false;

    public boolean isShortCommentsLoadOver() {
        return isShortCommentsLoadOver;
    }

    public CommentHelper(int newsId, int allNumOfLongComments, int allNumOfShortComments) {
        this.newsId = newsId;
        this.allNumOfLongComments = allNumOfLongComments;
        this.allNumOfShortComments = allNumOfShortComments;
    }

    public void obtainAllLongComments() {
        longComments.clear();
        longComments.addAll(CommentUtil.getAllLongComments(newsId));
    }

    public void obtainAllShortComments() {
        shortComments.clear();
        shortComments.addAll(CommentUtil.getAllShortComments(newsId));
    }

    //单次加载最多20条评论
    public void obtainShortCommentsByStep() {
        if (isShortCommentsLoadOver) {
            return;
        }
        List<Comment> comments = CommentUtil.getShortCommentByStep(newsId, shortComments.size() == 0 ? -1 : (shortComments.get(shortComments.size() - 1).getId()));
        currentLoadedShortComments = comments.size();
        if (comments.size() < 20) {
            isShortCommentsLoadOver = true;
        }
        shortComments.addAll(comments);
    }

    public void clearAllShortComments() {
        shortComments.clear();
        currentLoadedShortComments = 0;
        isShortCommentsLoadOver = false;
    }
}
