package com.sion.zhdaily.helpers;

import com.sion.zhdaily.utils.NewsContentUtil;
import com.sion.zhdaily.utils.beans.NewsContent;

public class NewsContentHelper {

    //通过新闻ID获取新闻内容对象
    public NewsContent getNewsContentById(int id) {
        return new NewsContent(NewsContentUtil.getNewsContentJson(id), NewsContentUtil.getNewsContentExtraJson(id));
    }
}
