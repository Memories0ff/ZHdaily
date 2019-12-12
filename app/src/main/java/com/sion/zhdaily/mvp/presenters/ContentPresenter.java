package com.sion.zhdaily.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import com.sion.zhdaily.models.beans.NewsContent;
import com.sion.zhdaily.models.beans.NewsSummary;
import com.sion.zhdaily.mvp.models.ContentModel;
import com.sion.zhdaily.mvp.views.IContentView;
import com.sion.zhdaily.utils.base.BasePresenter;

import java.util.ArrayList;


public class ContentPresenter extends BasePresenter<IContentView> {

    private ContentModel contentModel;

    public ContentPresenter(Activity activity) {
        this.contentModel = new ContentModel(activity);
    }

    //是否正在加载
    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }


    //---------------------------------------新闻数据---------------------------------------

    //当前新闻在array list中的位置
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    //当前新闻ID
    private int currentNewsId;

    public int getCurrentNewsId() {
        return currentNewsId;
    }

    public void setCurrentNewsId(int currentNewsId) {
        this.currentNewsId = currentNewsId;
    }

    //新闻简介对象
    private NewsSummary currentSummary;

    public NewsSummary getCurrentSummary() {
        return currentSummary;
    }

    public void setCurrentSummary(NewsSummary currentSummary) {
        this.currentSummary = currentSummary;
    }

    //array list中的新闻数
    private int newsNum;

    public int getNewsNum() {
        return newsNum;
    }

    public void setNewsNum(int newsNum) {
        this.newsNum = newsNum;
    }

    //新闻简介对象列表
    private ArrayList<NewsSummary> newsSummaries;

    public ArrayList<NewsSummary> getNewsSummaries() {
        return newsSummaries;
    }

    public void setNewsSummaries(ArrayList<NewsSummary> newsSummaries) {
        this.newsSummaries = newsSummaries;
    }

    //新闻内容对象
    private NewsContent currentContent;

    public NewsContent getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(NewsContent currentContent) {
        this.currentContent = currentContent;
    }

    //滑动至某一条新闻
    public void toNews(int index) {
        setIndex(index);
        setCurrentSummary(getNewsSummaries().get(getIndex()));
        setCurrentNewsId(getCurrentSummary().getId());
    }

    //---------------------------------------点赞操作---------------------------------------

    //是否已点赞
    private boolean isLiked = false;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    //添加新闻点赞记录
    public void insertNewsLikeRecord() {
        contentModel.insertNewsLikeRecord(getCurrentNewsId());
    }

    //删除新闻点赞记录
    public void deleteNewsLikeRecord() {
        contentModel.deleteNewsLikeRecord(getCurrentNewsId());
    }

    //查找是否存在新闻点赞记录
    public boolean findNewsLikeRecord() {
        return contentModel.findNewsLikeRecord(getCurrentNewsId());
    }

    //点击点赞
    public void clickLike() {
        //点赞或取消点赞
        if (!isLoading() && isLiked()) {
            deleteNewsLikeRecord();
            setLiked(false);
        } else {
            insertNewsLikeRecord();
            setLiked(true);
        }
        getView().uiUpdateAfterLike(isLiked());
    }


    //---------------------------------------网络回调---------------------------------------

    //网络状态
    private boolean isNetworkConnected = false;
    private ConnectivityManager connMgr = null;
    private NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    //注册网络回调
    public void registerConnMgr() {
        //网络状态
        connMgr = (ConnectivityManager) ((Activity) getView()).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connMgr.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isNetworkConnected = true;
        }
    }

    //注销网络回调
    public void unRegisterConnMgr() {
        connMgr.unregisterNetworkCallback(networkCallback);
    }

    //获取当前网络状态
    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    //网络回调类
    //改变网络状态时调用
    class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
//            Toast.makeText(NewsContentActivity.this, "网络已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
//            Toast.makeText(NewsContentActivity.this, "网络已断开", Toast.LENGTH_SHORT).show();
        }
    }

}
