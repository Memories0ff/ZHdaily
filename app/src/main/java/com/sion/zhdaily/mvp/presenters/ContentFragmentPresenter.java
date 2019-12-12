package com.sion.zhdaily.mvp.presenters;

import com.sion.zhdaily.models.beans.NewsContent;
import com.sion.zhdaily.mvp.models.ContentFragmentModel;
import com.sion.zhdaily.mvp.views.IContentFragmentView;
import com.sion.zhdaily.utils.base.BasePresenter;
import com.sion.zhdaily.views.activities.NewsContentActivity;
import com.sion.zhdaily.views.fragments.ContentFragment;

public class ContentFragmentPresenter extends BasePresenter<IContentFragmentView> {

    ContentFragmentModel contentFragmentModel;

    public ContentFragmentPresenter() {
        contentFragmentModel = new ContentFragmentModel();
    }


    //---------------------------------------新闻数据---------------------------------------

    //是否正在读取
    public void setLoading(boolean isLoading) {
        ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().setLoading(isLoading);
    }

    public boolean isLoading() {
        return ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().isLoading();
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
        //????????????????????????????????????????
        try {
            ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).getPresenter().setCurrentContent(content);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //????????????????????????????????????????
    }

    //下载内容并加载
    public void loadContent() {
        //????????????????????????????????????????
        setLoading(true);
        ((NewsContentActivity) ((ContentFragment) getView()).getActivity()).changeUIWhenSwitchFragment();
        new Thread(() -> {
            try {
                //下载新闻内容等数据
                setCurrentContent(contentFragmentModel.getNewsContentById(getNewsId()));
                //显示数据
                ((ContentFragment) getView()).getActivity().runOnUiThread(() -> {
                    try {
                        getView().updateUIAfterLoadingFinish();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } finally {
                        setLoading(false);
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                setLoading(false);
            }
        }).start();
        //????????????????????????????????????????
    }
}
