package com.sion.zhdaily.presenters;

import com.sion.zhdaily.models.NewsSummariesUtil;
import com.sion.zhdaily.models.beans.NewsSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public List<NewsSummary> topNewsSummariesList = new ArrayList<>();
    //其他非头条新闻列表
    public List<NewsSummary> newsSummariesList = new ArrayList<>();

    //加载了多少天的新闻
//    public int loadedDays = 0;
    //RecyclerView中插入数据的起始位置
    public int insertRangeStartPosition = 0;
    //本次加载了多少新闻
    public int loadedNewsSummaryNum = 0;

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

    //date转xx月xx日星期x
    public String dateMonthDayWeek(Date date) {
        return new SimpleDateFormat("MM月dd日 EEEE").format(date);
    }

    //逐天获取新闻。第一次加载的是头条和最新新闻，放在同一个List中，先放入的是头条新闻
    public void getNewsSummariesDayByDay() {
        //最新新闻和头条新闻未加载时，先加载
        if (!isTodayNewsSummariesLoaded) {
            topNewsSummariesList.addAll(NewsSummariesUtil.getTopNewsSummaries());
            List<NewsSummary> tempList = NewsSummariesUtil.getLatestNewsSummaries();
            //后面的为SUMMARIES_ITEM
            for (int i = 0; i < tempList.size(); i++) {
                //设置NewsSummary中未设置的属性dateStr和isFirstNewsSummary
                tempList.get(i).setDateStr("今日热闻");
                if (i == 0) {
                    tempList.get(i).setFirstNewsSummary(true);
                }
            }
            loadedNewsSummaryNum = tempList.size();
            newsSummariesList.addAll(tempList);
            //最新新闻和头条新闻设为已加载
            isTodayNewsSummariesLoaded = true;
        }
        //加载旧新闻
        else {
            insertRangeStartPosition = newsSummariesList.size()/* + 1*/;
            List<NewsSummary> tempList = NewsSummariesUtil.getOldNewsSummaries(currentLoadedNewsSummariesDateString);
            loadedNewsSummaryNum = tempList.size();
            for (int i = 0; i < tempList.size(); i++) {
                //设置NewsSummary中的属性dateStr和isFirstNewsSummary
                tempList.get(i).setDateStr(dateMonthDayWeek(toLastDay(currentLoadedNewsSummariesDate)));
                if (i == 0) {
                    tempList.get(i).setFirstNewsSummary(true);
                }
            }
            newsSummariesList.addAll(tempList);

            //日期减一天
            currentLoadedNewsSummariesDate = toLastDay(currentLoadedNewsSummariesDate);
            currentLoadedNewsSummariesDateString = dateToString(currentLoadedNewsSummariesDate);
        }
    }

    //更新新闻列表
    //成功返回true，失败返回false
    public boolean update() {
        //更新最新和头条新闻共用的Json
        if (NewsSummariesUtil.updateTodayNewsSummariesJson()) {
            //重置加载信息
            isTodayNewsSummariesLoaded = false;
            currentLoadedNewsSummariesDate = new Date();
            currentLoadedNewsSummariesDateString = dateToString(currentLoadedNewsSummariesDate);
            insertRangeStartPosition = 0;
            loadedNewsSummaryNum = 0;
            topNewsSummariesList.clear();
            newsSummariesList.clear();
            //获取头条和最新新闻列表
            getNewsSummariesDayByDay();
            return true;
        }
        return false;
    }

    //更新视图接口

}
