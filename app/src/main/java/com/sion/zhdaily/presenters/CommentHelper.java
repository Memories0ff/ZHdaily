package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.CommentUtil;
import com.sion.zhdaily.models.beans.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentHelper {

    public List<Comment> longComments = new ArrayList<>();
    public List<Comment> shortComments = new ArrayList<>();

    //当前加载短评数
    public int currentLoadedShortComments = 0;

    private boolean isShortCommentsLoadOver = false;

    public boolean isShortCommentsLoadOver() {
        return isShortCommentsLoadOver;
    }

    public void obtainAllLongComments(int newsId) {
        longComments.clear();
        longComments.addAll(CommentUtil.getAllLongComments(newsId));
    }

    public void obtainAllShortComments(int newsId) {
        shortComments.clear();
        shortComments.addAll(CommentUtil.getAllShortComments(newsId));
    }

    //单次加载最多20条评论
    public void obtainShortCommentsByStep(int newsId) {
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
}
