package com.sion.zhdaily.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {

    private Context context;
    private SQLiteOpenHelper sqLiteOpenHelper;

    public DBHelper(Context context) {
        this.context = context;
        this.sqLiteOpenHelper = new ZHDailySQLiteOpenHelper(context);
    }

    //添加评论点赞记录
    public void insertCommentLikeRecord(int authorId, long commentTime) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.execSQL(
            "insert into " + ZHDailySQLiteOpenHelper.COMMENTS_LIKED_TABLE + "(authorId,commentTime)"
                + " values(" + authorId + "," + commentTime + ");"
        );
        db.close();
    }

    //删除评论点赞记录
    public void deleteCommentLikeRecord(int authorId, long commentTime) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.execSQL(
            "delete from " + ZHDailySQLiteOpenHelper.COMMENTS_LIKED_TABLE + " where authorId="
                + authorId + " and commentTime=" + commentTime + ";"
        );
        db.close();
    }

    //查找是否存在点赞记录
    public boolean findCommentLikeRecord(int authorId, long commentTime) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "select count(*) from " + ZHDailySQLiteOpenHelper.COMMENTS_LIKED_TABLE
                + " where authorId=" + authorId + " and commentTime=" + commentTime + ";", null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        db.close();
        return result != 0;
    }

    //添加新闻点赞记录
    public void insertNewsLikeRecord(int newsId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.execSQL(
            "insert into " + ZHDailySQLiteOpenHelper.NEWS_LIKED_TABLE + "(newsId)"
                + " values(" + newsId + ");"
        );
        db.close();
    }

    //删除新闻点赞记录
    public void deleteNewsLikeRecord(int newsId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.execSQL(
            "delete from " + ZHDailySQLiteOpenHelper.NEWS_LIKED_TABLE + " where newsId="
                + newsId + ";"
        );
        db.close();
    }

    //查找是否存在新闻点赞记录
    public boolean findNewsLikeRecord(int newsId) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + ZHDailySQLiteOpenHelper.NEWS_LIKED_TABLE
            + " where newsId=" + newsId + ";", null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        db.close();
        return result != 0;
    }


    public static class ZHDailySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "ZHdaily.db";
        public static final String COMMENTS_LIKED_TABLE = "CommentsLikedTable";
        public static final String NEWS_LIKED_TABLE = "NewsLikedTable";

        public ZHDailySQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists " + COMMENTS_LIKED_TABLE + " (" +
                "authorId INTEGER," +
                "commentTime INTEGER," +
                "primary key (authorId,commentTime)" +
                ")");
            db.execSQL("create table if not exists " + NEWS_LIKED_TABLE + " (" +
                "newsId INTEGER primary key" +
                ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
