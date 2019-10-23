package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.CommentUtil;
import com.sion.zhdaily.models.beans.Comment;

import java.util.List;

public class CommentHelper {

    public List<Comment> longComments = null;
    public List<Comment> shortComments = null;

    public void obtainAllLongComments(int newsId) {
        longComments = CommentUtil.getAllLongComments(newsId);
    }

    public void obtainAllShortComments(int newsId) {
        shortComments = CommentUtil.getAllShortComments(newsId);
    }
}
