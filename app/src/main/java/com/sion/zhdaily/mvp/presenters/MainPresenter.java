package com.sion.zhdaily.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import com.sion.zhdaily.mvp.models.MainModel;
import com.sion.zhdaily.mvp.views.IMainView;
import com.sion.zhdaily.tools.base.BasePresenter;
import com.sion.zhdaily.utils.beans.NewsSummary;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter<IMainView> {

    private MainModel mainModel;

    public MainPresenter() {
        this.mainModel = new MainModel();
    }

    //---------------------------------------加载内容---------------------------------------

    //加载初始内容
    public void loadInitialData() {
        //初始加载，从网络下载数据并显示
        if (isNetworkConnected) {
            getView().uiSwitchToLoading(false);
            new Thread(() -> {
                //加载三天
                mainModel.getNewsSummariesDayByDay();
                ((Activity) getView()).runOnUiThread(() -> {
                    getView().updateForInsert();
                });
                mainModel.getNewsSummariesDayByDay();
                ((Activity) getView()).runOnUiThread(() -> {
                    getView().insertForSummaries();
                });
                mainModel.getNewsSummariesDayByDay();
                ((Activity) getView()).runOnUiThread(() -> {
                    getView().insertForSummaries();
                    getView().uiSwitchToNotLoading();
                });
            }).start();
        } else {
            getView().toast("网络不可用");
        }
    }

    //加载内容
    public void loadData() {
        mainModel.getNewsSummariesDayByDay();
    }

    //---------------------------------------更新---------------------------------------

    //更新所有内容
    public void update() {
        if (isNetworkConnected) {
            getView().uiSwitchToLoading(true);
            new Thread(() -> {
                if (mainModel.update()) {
                    ((Activity) getView()).runOnUiThread(() -> {
                        getView().updateForChangeAll();
                        getView().uiSwitchToNotLoading();
                        getView().toast("更新完成");
                    });
                } else {
                    ((Activity) getView()).runOnUiThread(() -> {
                        getView().uiSwitchToNotLoading();
                        getView().toast("加载失败");
                    });
                }
            }).start();
        } else {
            getView().uiSwitchToNotLoading();
            getView().toast("网络不可用");
        }
    }

    //---------------------------------------获取新闻加载信息---------------------------------------

    //获取数据源中新添加的新闻开始位置
    public int getInsertRangeStartPosition() {
        return mainModel.getInsertRangeStartPosition();
    }

    //获取数据源中新添加的新闻个数
    public int getLoadedNewsSummaryNum() {
        return mainModel.getLoadedNewsSummaryNum();
    }

    //获取新闻简介数据源
    public ArrayList<NewsSummary> getSummariesDataSource() {
        return mainModel.getSummariesDataSource();
    }

    //获取头条新闻数据源
    public List<NewsSummary> getTopNewsDataSource() {
        return mainModel.getTopNewsDataSource();
    }

    //---------------------------------------网络回调---------------------------------------

    private boolean isNetworkConnected = false;
    private ConnectivityManager connMgr = null;
    private NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    //注册网络回调
    public void registerConnMgr() {
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
//            Toast.makeText(MainActivity.this, "网络已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
//            Toast.makeText(MainActivity.this, "网络已断开", Toast.LENGTH_SHORT).show();
        }
    }


}
