package com.sion.zhdaily.views.adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sion.zhdaily.R;
import com.sion.zhdaily.helpers.CommentHelper;
import com.sion.zhdaily.helpers.DBHelper;
import com.sion.zhdaily.models.beans.Comment;
import com.sion.zhdaily.views.activities.CommentsActivity;
import com.sion.zhdaily.views.views.CommentRecyclerView;

import java.util.ArrayList;


public class CommentRvAdapter extends RecyclerView.Adapter {

    private CommentsActivity mActivity;
    private CommentRecyclerView mRv;
    private CommentHelper mHelper;
    private boolean isLoading = false;
    private boolean isShortsCommentsExpanded = false;
    //用于在首次加载过程中隐藏没有长评的提示图片，加载后如果没有长评则显示图片
    private NoneLongPlaceHolderVH noneLongPlaceHolderVH;

    private ArrayList<StateHolder> stateHolders = new ArrayList<>();

    private DBHelper dbHelper;

    enum ITEMTYPE {
        COMMENT, LONG_GROUP_TITLE, SHORT_GROUP_TITLE, NONE_LONG_PLACEHOLDER
    }


    public CommentRvAdapter(CommentsActivity mActivity, CommentRecyclerView mRv, CommentHelper mHelper) {
        this.mActivity = mActivity;
        this.mRv = mRv;
        this.mHelper = mHelper;
        this.dbHelper = new DBHelper(mActivity);
        this.mRv.setOnScrollToBottomListener(() -> {
            if (mActivity.isNetworkConnected()) {
                if (isShortsCommentsExpanded && !isLoading) {
                    isLoading = true;
                    new Thread(() -> {
                        mHelper.obtainShortCommentsByStep();
                        mActivity.runOnUiThread(() -> {
                            notifyItemRangeInserted(2 + Math.max(1, mHelper.longComments.size()) + mHelper.shortComments.size(), mHelper.currentLoadedShortComments);
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
        if (viewType == ITEMTYPE.COMMENT.ordinal()) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.rv_comment_item, parent, false);
            return new CommentVH(view);
        } else if (viewType == ITEMTYPE.LONG_GROUP_TITLE.ordinal()) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.rv_long_comment_group_title_item, parent, false);
            return new LongGroupTitleVH(view);
        } else if (viewType == ITEMTYPE.SHORT_GROUP_TITLE.ordinal()) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.rv_short_comment_group_title_item, parent, false);
            return new ShortGroupTitleVH(view);
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.rv_none_long_placeholder_item, parent, false);
            noneLongPlaceHolderVH = new NoneLongPlaceHolderVH(view);
            return noneLongPlaceHolderVH;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEMTYPE.COMMENT.ordinal()) {
            int realPos = getCommentPosition(position);

            Comment comment;
            if (realPos < mHelper.longComments.size()) {
                comment = mHelper.longComments.get(realPos);
            } else {
                comment = mHelper.shortComments.get(realPos - mHelper.longComments.size());
            }

            StateHolder stateHolder;
            if (realPos < stateHolders.size()) {
                stateHolder = stateHolders.get(realPos);
            } else {
                stateHolder = new StateHolder(stateHolders);
                stateHolder.setLiked(comment.isLiked());
            }


            CommentVH commentVH = (CommentVH) holder;


            ImageView ivThumb = commentVH.getIvThumb();
            if (stateHolder.isLiked()) {
                ivThumb.setImageResource(R.mipmap.thumb_blue);
                commentVH.getTvPopularityNum().setText("" + (comment.getLikes() + 1));
            } else {
                ivThumb.setImageResource(R.mipmap.thumb_gray);
                commentVH.getTvPopularityNum().setText("" + comment.getLikes());
            }


            //点击弹出菜单
            commentVH.getLlPopCommentMenuBtn().setOnClickListener((v -> {
                final String[] itemsStr = {comment.isLiked() ? "取消赞同" : "赞同", "举报", "复制", "回复"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(mActivity);
                listDialog.setItems(itemsStr, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if ("赞同".equals(itemsStr[which])) {
                                dbHelper.insertCommentLikeRecord(comment.getId(), comment.getTime());
                                ivThumb.setImageResource(R.mipmap.thumb_blue);
                                commentVH.getTvPopularityNum().setText("" + (comment.getLikes() + 1));
                                comment.setLiked(true);
                            } else {
                                dbHelper.deleteCommentLikeRecord(comment.getId(), comment.getTime());
                                ivThumb.setImageResource(R.mipmap.thumb_gray);
                                commentVH.getTvPopularityNum().setText("" + comment.getLikes());
                                comment.setLiked(false);
                            }
                            break;
                        case 1:
                            Toast.makeText(mActivity, "举报", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setText(comment.getContent().trim());
                            Toast.makeText(mActivity, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(mActivity, "回复", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
                listDialog.show();
            }));

            //防止在挂靠的Activity已被销毁的情况下使用Glide
            if (!mActivity.isDestroyed()) {
                Glide.with(mActivity)
                        .load(comment.getAvatarUrl())
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(commentVH.getIvAuthorPic());
            }

            commentVH.getTvAuthor().setText(comment.getAuthor());


            commentVH.getTvComment().setText(comment.getContent());


            TextView tvExpandBtn = commentVH.getTvOpenCloseBtn();
            TextView tvReplyComment = commentVH.getTvReplyComment();


            tvExpandBtn.setOnClickListener(null);
            tvExpandBtn.setOnClickListener((v) -> {
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
                    tvExpandBtn.setVisibility(View.GONE);
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
                    tvReplyComment.post(null);
                    tvReplyComment.post(() -> {
                        //当maxLine<实际行数时，getLineCount()返回maxLine；
                        //当maxLine>实际行数时，getLineCount()返回实际行数；
                        //复用前TextView setMaxLine(2)导致复用后就算实际行数>2，getLineCount()方法也只返回maxLine的值：2
                        //所以在onViewRecycled方法中调用setMaxLine(100)，使得复用后的TextView调用getLineCount()方法返回正确的行数
                        int lineCount = tvReplyComment.getLineCount();
                        if (lineCount <= 2) {
                            tvReplyComment.setMaxLines(lineCount);
                            tvExpandBtn.setVisibility(View.GONE);
                            stateHolder.setDisplayedReplyCommentLine(lineCount);
                            stateHolder.setNeedExpand(false);
                        } else {
                            tvReplyComment.setMaxLines(2);
                            tvExpandBtn.setVisibility(View.VISIBLE);
                            tvExpandBtn.setText("展开");
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
                        tvExpandBtn.setVisibility(View.VISIBLE);
                        if (stateHolder.isExpanded()) {
                            tvExpandBtn.setText("收起");
                        } else {
                            tvExpandBtn.setText("展开");
                        }
                    } else {
                        tvExpandBtn.setVisibility(View.GONE);
                    }
                } else {
                    tvExpandBtn.setVisibility(View.GONE);
                    tvReplyComment.setVisibility(View.GONE);
                }
            }


            commentVH.getTvTime().setText("" + comment.getTimeStr());

            stateHolder.setFirstCreated(false);
        } else if (getItemViewType(position) == ITEMTYPE.LONG_GROUP_TITLE.ordinal()) {
            LongGroupTitleVH longGroupTitleVH = (LongGroupTitleVH) holder;
            longGroupTitleVH.getTvLongCommentGroupTitle().setText(mHelper.allNumOfLongComments + "条长评");
        } else if (getItemViewType(position) == ITEMTYPE.SHORT_GROUP_TITLE.ordinal()) {
            ShortGroupTitleVH shortGroupTitleVH = (ShortGroupTitleVH) holder;
            shortGroupTitleVH.getTvShortCommentGroupTitle().setText(mHelper.allNumOfShortComments + "条短评");
            shortGroupTitleVH.getLlExpandBtn().setOnClickListener((v) -> {
                if (mActivity.isNetworkConnected()) {
                    if (!isShortsCommentsExpanded) {
                        if (!isLoading) {
                            isLoading = true;
                            shortGroupTitleVH.getPbShortCommentsLoading().setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                mHelper.obtainShortCommentsByStep();
                                ((CommentsActivity) v.getContext()).runOnUiThread(() -> {
                                    shortGroupTitleVH.getIvExpandingPic().setImageResource(R.mipmap.unfold_less_black_48);
                                    notifyItemRangeInserted(2 + Math.max(1, mHelper.longComments.size()), mHelper.currentLoadedShortComments);
                                    shortGroupTitleVH.getPbShortCommentsLoading().setVisibility(View.INVISIBLE);
                                    isShortsCommentsExpanded = true;
                                    isLoading = false;
                                });

                            }).start();
                        }
                    } else {
                        if (!isLoading) {
                            notifyItemRangeRemoved(2 + Math.max(1, mHelper.longComments.size()), mHelper.shortComments.size());
                            stateHolders.subList(mHelper.longComments.size(), stateHolders.size()).clear();
                            mHelper.clearAllShortComments();
                            shortGroupTitleVH.getIvExpandingPic().setImageResource(R.mipmap.unfold_more_black_48);
                            isShortsCommentsExpanded = false;
                        }
                    }
                } else {
                    Toast.makeText(mActivity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            });
            if (isShortsCommentsExpanded) {
                shortGroupTitleVH.getIvExpandingPic().setImageResource(R.mipmap.unfold_less_black_48);
            } else {
                shortGroupTitleVH.getIvExpandingPic().setImageResource(R.mipmap.unfold_more_black_48);
            }
        }
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof CommentVH) {
            TextView tvReply = ((CommentVH) holder).getTvReplyComment();
            tvReply.setMaxLines(100000);
        }
        super.onViewRecycled(holder);
    }

    //在进入Activity后的加载中使用
    //在首次加载过程中隐藏没有长评的提示图片，加载后如果没有长评则显示图片
    public void notifyCommentSetChanged() {
        if (mHelper.longComments.isEmpty()) {
            noneLongPlaceHolderVH.getLlNoneCommentPlaceholder().setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
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

    private int getCommentPosition(int position) {
        if (position - 1 <= Math.max(1, mHelper.longComments.size())) {
            return position - 1;
        } else {
            return position - (mHelper.longComments.isEmpty() ? 3 : 2);
        }
    }

    private static class CommentVH extends RecyclerView.ViewHolder {

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

    private static class LongGroupTitleVH extends RecyclerView.ViewHolder {

        private TextView tvLongCommentGroupTitle = null;

        public TextView getTvLongCommentGroupTitle() {
            return tvLongCommentGroupTitle;
        }

        public LongGroupTitleVH(@NonNull View itemView) {
            super(itemView);
            tvLongCommentGroupTitle = itemView.findViewById(R.id.tv_longCommentGroupTitle);
        }
    }

    private static class ShortGroupTitleVH extends RecyclerView.ViewHolder {

        private LinearLayout llExpandBtn = null;
        private TextView tvShortCommentGroupTitle = null;
        private ImageView ivExpandingPic = null;
        private ProgressBar pbShortCommentsLoading = null;

        public LinearLayout getLlExpandBtn() {
            return llExpandBtn;
        }

        public TextView getTvShortCommentGroupTitle() {
            return tvShortCommentGroupTitle;
        }

        public ImageView getIvExpandingPic() {
            return ivExpandingPic;
        }

        public ProgressBar getPbShortCommentsLoading() {
            return pbShortCommentsLoading;
        }

        public ShortGroupTitleVH(@NonNull View itemView) {
            super(itemView);
            llExpandBtn = itemView.findViewById(R.id.ll_expandBtn);
            tvShortCommentGroupTitle = itemView.findViewById(R.id.tv_shortCommentGroupTitle);
            ivExpandingPic = itemView.findViewById(R.id.iv_expandingPic);
            pbShortCommentsLoading = itemView.findViewById(R.id.pb_shortCommentsLoading);
        }
    }

    private static class NoneLongPlaceHolderVH extends RecyclerView.ViewHolder {

        private LinearLayout llNoneCommentPlaceholder = null;

        public LinearLayout getLlNoneCommentPlaceholder() {
            return llNoneCommentPlaceholder;
        }

        public NoneLongPlaceHolderVH(@NonNull View itemView) {
            super(itemView);
            llNoneCommentPlaceholder = itemView.findViewById(R.id.ll_noneCommentPlaceholder);
        }
    }

    private static class StateHolder {

        //保存控件状态
        private boolean hasReply = false;
        private boolean isNeedExpand = false;
        private int displayedReplyCommentLine = 0;
        private int realReplyCommentLine = 0;
        private boolean isExpanded = false;
        private boolean isLiked = false;
        private boolean isFirstCreated = true;

        private ArrayList<StateHolder> stateHolders = null;

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
