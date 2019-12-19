package com.sion.zhdaily.mvp.views;

import com.sion.zhdaily.tools.base.IBaseView;

public interface IContentFragmentView extends IBaseView {

    void updateUIAfterLoadingFinish();

    void toast(String str);

    void loadingFailedHint(String str);

}
