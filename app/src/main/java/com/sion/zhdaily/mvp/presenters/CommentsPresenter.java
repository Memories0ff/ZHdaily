package com.sion.zhdaily.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import com.sion.zhdaily.helpers.CommentHelper;
import com.sion.zhdaily.mvp.models.CommentsModel;
import com.sion.zhdaily.mvp.views.ICommentsView;
import com.sion.zhdaily.tools.base.BasePresenter;
import com.sion.zhdaily.utils.beans.Comment;

import java.util.List;

public class CommentsPresenter extends BasePresenter<ICommentsView> {

    private CommentsModel commentsModel;

    //---------------------------------------加载评论操作---------------------------------------

    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isShortsCommentsExpanded = false;

    public boolean isShortsCommentsExpanded() {
        return isShortsCommentsExpanded;
    }

    public void setShortsCommentsExpanded(boolean shortsCommentsExpanded) {
        isShortsCommentsExpanded = shortsCommentsExpanded;
    }

    private int longCommentsNum;
    private int shortCommentsNum;

    public int getLongCommentsNum() {
        return longCommentsNum;
    }

    public int getShortCommentsNum() {
        return shortCommentsNum;
    }

    //初始化评论数据
    public void initCommentsData(int newsId, int longCommentsNum, int shortCommentsNum, Activity activity) {
        this.longCommentsNum = longCommentsNum;
        this.shortCommentsNum = shortCommentsNum;
        commentsModel = new CommentsModel(newsId, longCommentsNum, shortCommentsNum, activity);
    }

    //获取评论加载帮助类
    public CommentHelper getCommentHelper() {
        return commentsModel.getCommentHelper();
    }

    //获取当前已加载短评数
    public int getCurrentLoadedShortComments() {
        return commentsModel.getCurrentLoadedShortComments();
    }

    //获取长评数据源
    public List<Comment> getLongComments() {
        return commentsModel.getLongComments();
    }

    //获取短评数据源
    public List<Comment> getShortComments() {
        return commentsModel.getShortComments();
    }

    //获取所有长评
    public void obtainAllLongComments() {
        commentsModel.obtainAllLongComments();
    }

    //单次加载最多20条评论
    public void obtainShortCommentsByStep() {
        commentsModel.obtainShortCommentsByStep();
    }

    //清除所有短评
    public void clearAllShortComments() {
        commentsModel.clearAllShortComments();
    }

    //加载评论
    public void loadInitialComments() {
        if (isNetworkConnected()) {
            new Thread(() -> {
                getCommentHelper().obtainAllLongComments();
                ((Activity) getView()).runOnUiThread(() -> {
                    getView().uiChangeWhenInitialLoadingFinish();
                });
            }).start();
        } else {
            getView().toast("网络不可用");
        }
    }

    public void continueLoading() {
        if (isNetworkConnected()) {
            if (isShortsCommentsExpanded() && !isLoading()) {
                setLoading(true);
                new Thread(() -> {
                    obtainShortCommentsByStep();
                    ((Activity) getView()).runOnUiThread(() -> {
                        getView().uiChangeWhenContinueLoadingFinish();
                    });
                }).start();
            }
        } else {
            getView().toast("网络不可用");
        }
    }


    //---------------------------------------数据库操作---------------------------------------

    //添加评论点赞记录
    public void insertCommentLikeRecord(int authorId, long commentTime) {
        commentsModel.insertCommentLikeRecord(authorId, commentTime);
    }

    //删除评论点赞记录
    public void deleteCommentLikeRecord(int authorId, long commentTime) {
        commentsModel.deleteCommentLikeRecord(authorId, commentTime);
    }

    //查找是否存在点赞记录
    public boolean findCommentLikeRecord(int authorId, long commentTime) {
        return commentsModel.findCommentLikeRecord(authorId, commentTime);
    }

    //---------------------------------------网络回调---------------------------------------

    //网络状态
    private boolean isNetworkConnected = false;

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    private ConnectivityManager connMgr = null;
    private NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    public void registerConnMgr() {

        //网络状态
        connMgr = (ConnectivityManager) ((Activity) getView()).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connMgr.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isNetworkConnected = true;
        }
    }

    public void unregisterConnMgr() {
        connMgr.unregisterNetworkCallback(networkCallback);
    }

    class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
        }
    }
}
