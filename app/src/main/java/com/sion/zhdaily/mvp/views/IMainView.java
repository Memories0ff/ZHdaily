package com.sion.zhdaily.mvp.views;

import com.sion.zhdaily.utils.base.IBaseView;

public interface IMainView extends IBaseView {

    //Toast
    void toast(String str);

    //只增加简介
    void insertForSummaries();

    //增加简介和更新头条
    void updateForInsert();

    //更新简介和头条
    void updateForChangeAll();

    //设置UI为正在更新的状态，参数表示是否为继续加载，true为继续，false为更新
    void uiSwitchToLoading(boolean isContinue);

    //设置UI为不在更新的状态
    void uiSwitchToNotLoading();

}
