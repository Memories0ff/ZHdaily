package com.sion.zhdaily.mvp.presenters;

import com.sion.zhdaily.mvp.models.ContentFragmentModel;
import com.sion.zhdaily.mvp.views.IContentFragmentView;
import com.sion.zhdaily.tools.base.BasePresenter;
import com.sion.zhdaily.utils.beans.NewsContent;
import com.sion.zhdaily.views.activities.NewsContentActivity;
import com.sion.zhdaily.views.fragments.ContentFragment;

public class ContentFragmentPresenter extends BasePresenter<IContentFragmentView> {

    private ContentFragmentModel contentFragmentModel;

    private NewsContentActivity mActivity;

    public ContentFragmentPresenter(NewsContentActivity mActivity) {
        contentFragmentModel = new ContentFragmentModel();
        this.mActivity = mActivity;
    }

    public NewsContentActivity getActivity() {
        return mActivity;
    }

    //---------------------------------------新闻数据---------------------------------------

    //是否正在读取
    public void setLoading(boolean isLoading) {
        mActivity.getPresenter().setLoading(isLoading);
    }

    public boolean isLoading() {
        return mActivity.getPresenter().isLoading();
    }

    //当前新闻ID
    public int getNewsId() {
        return ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().getCurrentNewsId();
    }

    public void setNewsId(int newsId) {
        ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().setCurrentNewsId(newsId);
    }

    //新闻内容对象
    public NewsContent getCurrentContent() {
        return ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().getCurrentContent();
    }

    public void setCurrentContent(NewsContent content) {
        //此时Fragment可能已经不存在
        if (getView() != null) {
            ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().setCurrentContent(content);
        }
    }

    //下载内容并加载
    public void loadContent() {
        if (!isNetworkConnected()) {
            getView().loadingFailedHint("网络不可用，加载失败");
        } else {
            setLoading(true);
            ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).changeUIWhenSwitchFragment();
            new Thread(() -> {
                //下载新闻内容等数据
                setCurrentContent(contentFragmentModel.getNewsContentById(getNewsId()));
                //显示数据
                getActivity().runOnUiThread(() -> {
                    //此时Fragment可能已经不存在
                    if (getView() != null) {
                        getView().updateUIAfterLoadingFinish();
                    }
                    setLoading(false);
                });
            }).start();
        }
    }

    //-------------------------------------网络状态-----------------------------------------
    public boolean isNetworkConnected() {
        return ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().isNetworkConnected();
    }
}
