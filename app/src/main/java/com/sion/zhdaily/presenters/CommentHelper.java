package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.CommentUtil;
import com.sion.zhdaily.models.beans.Comment;

import java.util.List;

public class CommentHelper {

    public List<Comment> longComments = null;
    public List<Comment> shortComments = null;

    private boolean isShortCommentsLoadOver = false;

    public boolean isShortCommentsLoadOver() {
        return isShortCommentsLoadOver;
    }

    public void obtainAllLongComments(int newsId) {
        longComments = CommentUtil.getAllLongComments(newsId);
    }

    public void obtainAllShortComments(int newsId) {
        shortComments = CommentUtil.getAllShortComments(newsId);
    }

    //单次加载最多20条评论
    public void obtainShortCommentsByStep(int newsId) {
        if (isShortCommentsLoadOver) {
            return;
        }
        List<Comment> comments = CommentUtil.getShortCommentByStep(newsId, shortComments.size() == 0 ? -1 : (shortComments.get(shortComments.size() - 1).getId()));
        if (comments.size() < 20) {
            isShortCommentsLoadOver = true;
        }
        shortComments.addAll(comments);
    }
}
