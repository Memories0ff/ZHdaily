package com.sion.zhdaily.views.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.Comment;
import com.sion.zhdaily.presenters.CommentHelper;
import com.sion.zhdaily.views.activities.CommentsActivity;
import com.sion.zhdaily.views.views.CommentRecyclerView;

import java.util.ArrayList;


public class CommentRvAdapter extends RecyclerView.Adapter {

    private CommentsActivity mActivity;
    private CommentRecyclerView mRv;
    private CommentHelper mHelper;
    private boolean isLoading = false;

    private ArrayList<StateHolder> stateHolders = new ArrayList<>();

    enum ITEMTYPE {
        COMMENT, LONG_GROUP_TITLE, SHORT_GROUP_TITLE, NONE_LONG_PLACEHOLDER
    }


    public CommentRvAdapter(CommentsActivity mActivity, CommentRecyclerView mRv, CommentHelper mHelper) {
        this.mActivity = mActivity;
        this.mRv = mRv;
        this.mHelper = mHelper;
        this.mRv.setOnScrollToBottomListener(() -> {
            if (mActivity.isNetworkConnected()) {
                if (!isLoading) {
                    isLoading = true;
                    new Thread(() -> {
                        mHelper.obtainShortCommentsByStep(mActivity.newsId);
                        mActivity.runOnUiThread(() -> {
                            notifyItemRangeInserted(mHelper.longComments.size() + mHelper.shortComments.size(), mHelper.currentLoadedShortComments);
                            isLoading = false;
                        });
                    }).start();
                }
            } else {
                Toast.makeText(mActivity, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.rv_comment_item, parent, false);
        return new CommentVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment;
        if (position < mHelper.longComments.size()) {
            comment = mHelper.longComments.get(position);
        } else {
            comment = mHelper.shortComments.get(position - mHelper.longComments.size());
        }
        StateHolder stateHolder = position < stateHolders.size() ? stateHolders.get(position) : new StateHolder(stateHolders);


        CommentVH commentVH = (CommentVH) holder;

        commentVH.getLlPopCommentMenuBtn().setOnClickListener((v -> Toast.makeText(mActivity, "弹出菜单", Toast.LENGTH_SHORT).show()));

        Glide.with(mActivity)
                .load(comment.getAvatarUrl())
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(commentVH.getIvAuthorPic());

        commentVH.getTvAuthor().setText(comment.getAuthor());

        ImageView ivThumb = commentVH.getIvThumb();
        if (stateHolder.isLiked()) {
            ivThumb.setImageResource(R.mipmap.thumb_blue);
        } else {
            ivThumb.setImageResource(R.mipmap.thumb_gray);
        }

        commentVH.getTvPopularityNum().setText("" + comment.getLikes());

        commentVH.getTvComment().setText(comment.getContent());

        //???????????????????????????????????????????????????????????????????????????
        TextView tvOpenCloseBtn = commentVH.getTvOpenCloseBtn();
        TextView tvReplyComment = commentVH.getTvReplyComment();
        tvOpenCloseBtn.setOnClickListener((v) -> {
            stateHolder.setNeedExpand(true);
            TextView tv = (TextView) v;
            if (tv.getText().equals("展开")) {
                tv.setText("收起");
                tvReplyComment.setMaxLines(stateHolder.getRealReplyCommentLine());
                stateHolder.setExpanded(true);
                stateHolder.setDisplayedReplyCommentLine(stateHolder.getRealReplyCommentLine());
            } else {
                tv.setText("展开");
                tvReplyComment.setMaxLines(2);
                stateHolder.setExpanded(false);
                stateHolder.setDisplayedReplyCommentLine(2);
            }
        });

        if (stateHolder.isFirstCreated()) {
            if (comment.getReplyAuthorId() == 0) {
                tvOpenCloseBtn.setVisibility(View.GONE);
                tvReplyComment.setVisibility(View.GONE);
                stateHolder.setHasReply(false);
                stateHolder.setExpanded(false);
                stateHolder.setNeedExpand(false);
                stateHolder.setDisplayedReplyCommentLine(0);
                stateHolder.setRealReplyCommentLine(0);
            } else {
                tvReplyComment.setVisibility(View.VISIBLE);
                SpannableString spannableString = new SpannableString("//" + comment.getReplyAuthor() + "：" + comment.getReplyContent());
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 3 + comment.getReplyAuthor().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 + comment.getReplyAuthor().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvReplyComment.setText(spannableString);
                tvReplyComment.post(() -> {
                    //当maxLine<实际行数时，getLineCount()返回maxLine；
                    //当maxLine>实际行数时，getLineCount()返回实际行数；
                    //复用前TextView setMaxLine(2)导致复用后就算实际行数>2，getLineCount()方法也只返回maxLine的值：2
                    //所以在onViewRecycled方法中调用setMaxLine(100)，使得复用后的TextView调用getLineCount()方法返回正确的行数
                    int lineCount = tvReplyComment.getLineCount();
                    if (lineCount <= 2) {
                        tvReplyComment.setMaxLines(lineCount);
                        tvOpenCloseBtn.setVisibility(View.GONE);
                        stateHolder.setDisplayedReplyCommentLine(lineCount);
                        stateHolder.setNeedExpand(false);
                    } else {
                        tvReplyComment.setMaxLines(2);
                        tvOpenCloseBtn.setVisibility(View.VISIBLE);
                        tvOpenCloseBtn.setText("展开");
                        stateHolder.setDisplayedReplyCommentLine(2);
                        stateHolder.setNeedExpand(true);
                    }
                    stateHolder.setRealReplyCommentLine(lineCount);
                });
                stateHolder.setHasReply(true);
                stateHolder.setExpanded(false);
            }
        } else {
            if (stateHolder.isHasReply()) {
                tvReplyComment.setVisibility(View.VISIBLE);
                SpannableString spannableString = new SpannableString("//" + comment.getReplyAuthor() + "：" + comment.getReplyContent());
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 3 + comment.getReplyAuthor().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 + comment.getReplyAuthor().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvReplyComment.setText(spannableString);
                tvReplyComment.setMaxLines(stateHolder.getDisplayedReplyCommentLine());
                if (stateHolder.isNeedExpand()) {
                    tvOpenCloseBtn.setVisibility(View.VISIBLE);
                    if (stateHolder.isExpanded()) {
                        tvOpenCloseBtn.setText("收起");
                    } else {
                        tvOpenCloseBtn.setText("展开");
                    }
                } else {
                    tvOpenCloseBtn.setVisibility(View.GONE);
                }
            } else {
                tvOpenCloseBtn.setVisibility(View.GONE);
                tvReplyComment.setVisibility(View.GONE);
            }
        }

        //???????????????????????????????????????????????????????????????????????????


        commentVH.getTvTime().setText("" + comment.getTime());

        stateHolder.setFirstCreated(false);

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof CommentVH) {
            TextView tvReply = ((CommentVH) holder).getTvReplyComment();
            tvReply.setMaxLines(100000);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 2 + Math.max(mHelper.longComments.size(), 1) + mHelper.shortComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEMTYPE.LONG_GROUP_TITLE.ordinal();
        }
        if (mHelper.longComments.isEmpty()) {
            if (position == 1) {
                return ITEMTYPE.NONE_LONG_PLACEHOLDER.ordinal();
            } else if (position == 2) {
                return ITEMTYPE.SHORT_GROUP_TITLE.ordinal();
            } else {
                return ITEMTYPE.COMMENT.ordinal();
            }
        } else {
            if (position <= mHelper.longComments.size()) {
                return ITEMTYPE.COMMENT.ordinal();
            } else if (position == mHelper.longComments.size() + 1) {
                return ITEMTYPE.SHORT_GROUP_TITLE.ordinal();
            } else {
                return ITEMTYPE.COMMENT.ordinal();
            }
        }
    }

    class CommentVH extends RecyclerView.ViewHolder {

        //控件
        private LinearLayout llPopCommentMenuBtn = null;
        private ImageView ivAuthorPic = null;
        private TextView tvAuthor = null;
        private ImageView ivThumb = null;
        private TextView tvPopularityNum = null;
        private TextView tvComment = null;
        private TextView tvReplyComment = null;
        private TextView tvTime = null;
        private TextView tvOpenCloseBtn = null;

        public LinearLayout getLlPopCommentMenuBtn() {
            return llPopCommentMenuBtn;
        }

        public ImageView getIvAuthorPic() {
            return ivAuthorPic;
        }

        public TextView getTvAuthor() {
            return tvAuthor;
        }

        public ImageView getIvThumb() {
            return ivThumb;
        }

        public TextView getTvPopularityNum() {
            return tvPopularityNum;
        }

        public TextView getTvComment() {
            return tvComment;
        }

        public TextView getTvReplyComment() {
            return tvReplyComment;
        }

        public TextView getTvTime() {
            return tvTime;
        }

        public TextView getTvOpenCloseBtn() {
            return tvOpenCloseBtn;
        }


        public CommentVH(@NonNull View itemView) {
            super(itemView);
            this.llPopCommentMenuBtn = itemView.findViewById(R.id.ll_popCommentMenuBtn);
            this.ivAuthorPic = itemView.findViewById(R.id.iv_authorPic);
            this.tvAuthor = itemView.findViewById(R.id.iv_author);
            this.ivThumb = itemView.findViewById(R.id.iv_thumb);
            this.tvPopularityNum = itemView.findViewById(R.id.tv_popularityNum);
            this.tvComment = itemView.findViewById(R.id.tv_comment);
            this.tvReplyComment = itemView.findViewById(R.id.tv_ReplyComment);
            this.tvTime = itemView.findViewById(R.id.tv_time);
            this.tvOpenCloseBtn = itemView.findViewById(R.id.tv_openCloseBtn);
        }
    }

    class LongGroupTitleVH extends RecyclerView.ViewHolder {

        private TextView tvLongCommentGroupTitle = null;

        public TextView getTvLongCommentGroupTitle() {
            return tvLongCommentGroupTitle;
        }

        public LongGroupTitleVH(@NonNull View itemView) {
            super(itemView);
            tvLongCommentGroupTitle = itemView.findViewById(R.id.tv_longCommentGroupTitle);
        }
    }

    class ShortGroupTitleVH extends RecyclerView.ViewHolder {

        private LinearLayout llExpandBtn = null;
        private TextView tvShortCommentGroupTitle = null;
        private ImageView ivExpandingPic = null;

        public LinearLayout getLlExpandBtn() {
            return llExpandBtn;
        }

        public TextView getTvShortCommentGroupTitle() {
            return tvShortCommentGroupTitle;
        }

        public ImageView getIvExpandingPic() {
            return ivExpandingPic;
        }

        public ShortGroupTitleVH(@NonNull View itemView) {
            super(itemView);
            llExpandBtn = itemView.findViewById(R.id.ll_expandBtn);
            tvShortCommentGroupTitle = itemView.findViewById(R.id.tv_shortCommentGroupTitle);
            ivExpandingPic = itemView.findViewById(R.id.iv_expandingPic);
        }
    }

    class NoneLongPlaceHolderVH extends RecyclerView.ViewHolder {

        public NoneLongPlaceHolderVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    class StateHolder {

        //保存控件状态
        private boolean hasReply = false;
        private boolean isNeedExpand = false;
        private int displayedReplyCommentLine = 0;
        private int realReplyCommentLine = 0;
        private boolean isExpanded = false;
        private boolean isLiked = false;
        private boolean isFirstCreated = true;

        private ArrayList<StateHolder> stateHolders = new ArrayList<>();

        public boolean isHasReply() {
            return hasReply;
        }

        public void setHasReply(boolean hasReply) {
            this.hasReply = hasReply;
        }

        public boolean isNeedExpand() {
            return isNeedExpand;
        }

        public void setNeedExpand(boolean needExpand) {
            isNeedExpand = needExpand;
        }

        public int getDisplayedReplyCommentLine() {
            return displayedReplyCommentLine;
        }

        public void setDisplayedReplyCommentLine(int displayedReplyCommentLine) {
            this.displayedReplyCommentLine = displayedReplyCommentLine;
        }

        public int getRealReplyCommentLine() {
            return realReplyCommentLine;
        }

        public void setRealReplyCommentLine(int realReplyCommentLine) {
            this.realReplyCommentLine = realReplyCommentLine;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isFirstCreated() {
            return isFirstCreated;
        }

        public void setFirstCreated(boolean firstCreated) {
            isFirstCreated = firstCreated;
        }

        public StateHolder(ArrayList<StateHolder> stateHolders) {
            this.stateHolders = stateHolders;
            this.stateHolders.add(this);
        }

    }

}
