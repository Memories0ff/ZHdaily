package com.sion.zhdaily.mvp.views;

import com.sion.zhdaily.tools.base.IBaseView;

public interface IContentView extends IBaseView {

    void uiUpdateAfterLike(boolean isLikeAfterClick);

    void changeUIWhenSwitchFragment();

}
