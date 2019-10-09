package com.sion.zhdaily.models;

import com.sion.zhdaily.models.beans.NewsSummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsJsonUtil {
    //从Json获取多个新闻简介
    public static List<NewsSummary> getNewsSummaries(String newsJson) {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(newsJson).getJSONArray("stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), false));
            }
            return newsSummaries;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //从Json获取所有头条新闻简介
    public static List<NewsSummary> getTopNewsSummaries(String latestNewsJson) {
        List<NewsSummary> newsSummaries = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(latestNewsJson).getJSONArray("top_stories");
            for (int i = 0; i < array.length(); i++) {
                newsSummaries.add(new NewsSummary(array.getJSONObject(i).toString(), true));
            }
            return newsSummaries;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
