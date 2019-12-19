package com.sion.zhdaily.mvp.models;

import com.sion.zhdaily.helpers.NewsSummariesHelper;
import com.sion.zhdaily.tools.base.BaseModel;
import com.sion.zhdaily.utils.beans.NewsSummary;

import java.util.ArrayList;

public class MainModel extends BaseModel {

    //加载数据帮助类
    NewsSummariesHelper helper;

    public MainModel() {
        helper = new NewsSummariesHelper();
    }

    //按天数依次加载
    public void getNewsSummariesDayByDay() {
        helper.getNewsSummariesDayByDay();
    }

    //获取数据源中新添加的新闻开始位置
    public int getInsertRangeStartPosition() {
        return helper.insertRangeStartPosition;
    }

    //获取数据源中新添加的新闻个数
    public int getLoadedNewsSummaryNum() {
        return helper.insertRangeStartPosition;
    }

    //获取新闻简介数据源
    public ArrayList<NewsSummary> getSummariesDataSource() {
        return helper.newsSummariesList;
    }

    //获取头条新闻数据源
    public ArrayList<NewsSummary> getTopNewsDataSource() {
        return helper.topNewsSummariesList;
    }

    //更新所有数据
    public boolean update() {
        return helper.update();
    }
}
