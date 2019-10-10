package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.NewsSummariesUtil;
import com.sion.zhdaily.models.beans.NewsSummary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class NewsSummariesHelper {
    //最近加载的新闻的日期（yyyyMMdd）
    public String currentLoadedNewsSummariesDateString;
    //最近加载的新闻的日期Date对象
    public Date currentLoadedNewsSummariesDate;
    //最新新闻和头条新闻是否加载
    public boolean isTodayNewsSummariesLoaded;

//    //getNewsSummaries()方法将头条新闻和最新新闻放在同一个List中，需要各自的数量分别开，List中先放入头条新闻
//    //头条新闻数
//    public int topNewsNum;
//    //最新新闻数
//    public int latestNewsNum;

    //头条新闻列表
    public List<NewsSummary> topNewsSummariesList = null;
    //其他非头条新闻列表
    public List<NewsSummary> newsSummariesList = null;

    public NewsSummariesHelper() {
        isTodayNewsSummariesLoaded = false;
        currentLoadedNewsSummariesDate = new Date();
        currentLoadedNewsSummariesDateString = dateToString(currentLoadedNewsSummariesDate);
    }

    //日期加一天
    public Date toNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    //日期减一天
    public Date toLastDay(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    //date转八位日期字符串
    public String dateToString(Date date) {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    //逐天获取新闻。第一次加载的是头条和最新新闻，放在同一个List中，先放入的是头条新闻
    public void getNewsSummaries() {
        //最新新闻和头条新闻未加载时，先加载
        if (!isTodayNewsSummariesLoaded) {
            topNewsSummariesList = NewsSummariesUtil.getTopNewsSummaries();
            newsSummariesList = NewsSummariesUtil.getLatestNewsSummaries();
            //最新新闻和头条新闻设为已加载
            isTodayNewsSummariesLoaded = true;
        }
        //加载旧新闻
        else {
            newsSummariesList.addAll(NewsSummariesUtil.getOldNewsSummaries(currentLoadedNewsSummariesDateString));
            //日期减一天
            currentLoadedNewsSummariesDate = toLastDay(currentLoadedNewsSummariesDate);
            currentLoadedNewsSummariesDateString = dateToString(currentLoadedNewsSummariesDate);
        }
    }

    //更新新闻列表
    public void update() {
        //更新最新和头条新闻共用的Json
        NewsSummariesUtil.updateTodayNewsSummariesJson();
        //重置加载信息
        isTodayNewsSummariesLoaded = false;
        currentLoadedNewsSummariesDate = new Date();
        currentLoadedNewsSummariesDateString = dateToString(currentLoadedNewsSummariesDate);
        //获取头条和最新新闻列表
        getNewsSummaries();
    }

    //更新视图接口

}
