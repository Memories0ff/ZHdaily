package com.sion.zhdaily.mvp.views;

import com.sion.zhdaily.utils.base.IBaseView;

public interface IContentView extends IBaseView {

    void uiUpdateAfterLike(boolean isLikeAfterClick);

    void changeUIWhenSwitchFragment();

}
