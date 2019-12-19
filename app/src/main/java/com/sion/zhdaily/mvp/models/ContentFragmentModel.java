package com.sion.zhdaily.mvp.models;

import com.sion.zhdaily.helpers.NewsContentHelper;
import com.sion.zhdaily.tools.base.BaseModel;
import com.sion.zhdaily.utils.beans.NewsContent;

public class ContentFragmentModel extends BaseModel {
    //新闻内容帮助类
    NewsContentHelper helper;

    public ContentFragmentModel() {
        this.helper = new NewsContentHelper();
    }

    //通过新闻ID获取新闻内容对象
    public NewsContent getNewsContentById(int id) {
        return helper.getNewsContentById(id);
    }
}
