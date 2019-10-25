package com.sion.zhdaily.views.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sion.zhdaily.models.beans.Comment;
import com.sion.zhdaily.presenters.CommentHelper;
import com.sion.zhdaily.views.activities.CommentsActivity;
import com.sion.zhdaily.views.views.CommentRecyclerView;

import java.util.List;

public class CommentRvAdapter extends RecyclerView.Adapter {

    private CommentsActivity mActivity = null;
    private List<Comment> mComments = null;
    private CommentRecyclerView mRv = null;
    private CommentHelper mHelper = null;

    public CommentRvAdapter(CommentsActivity mActivity, List<Comment> mComments, CommentRecyclerView mRv, CommentHelper mHelper) {
        this.mActivity = mActivity;
        this.mComments = mComments;
        this.mRv = mRv;
        this.mHelper = mHelper;
        this.mRv.setOnScrollToBottomListener(() -> {
            mHelper.obtainShortCommentsByStep(mActivity.newsId);
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CommentVH extends RecyclerView.ViewHolder {

        public CommentVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
