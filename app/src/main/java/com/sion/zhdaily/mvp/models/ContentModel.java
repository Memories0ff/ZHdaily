package com.sion.zhdaily.mvp.models;

import android.content.Context;

import com.sion.zhdaily.helpers.DBHelper;
import com.sion.zhdaily.tools.base.BaseModel;

public class ContentModel extends BaseModel {
    //数据库操作帮助类
    DBHelper dbHelper;

    public ContentModel(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    //添加新闻点赞记录
    public void insertNewsLikeRecord(int newsId) {
        dbHelper.insertNewsLikeRecord(newsId);
    }

    //删除新闻点赞记录
    public void deleteNewsLikeRecord(int newsId) {
        dbHelper.deleteNewsLikeRecord(newsId);
    }

    //查找是否存在新闻点赞记录
    public boolean findNewsLikeRecord(int newsId) {
        return dbHelper.findNewsLikeRecord(newsId);
    }

}
