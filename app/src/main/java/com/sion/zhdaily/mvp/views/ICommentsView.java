package com.sion.zhdaily.mvp.views;

import com.sion.zhdaily.utils.base.IBaseView;

public interface ICommentsView extends IBaseView {

    void toast(String str);

    void uiChangeWhenInitialLoadingFinish();

    void uiChangeWhenContinueLoadingFinish();

}
