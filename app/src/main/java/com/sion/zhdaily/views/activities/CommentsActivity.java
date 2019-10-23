package com.sion.zhdaily.views.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.Comment;
import com.sion.zhdaily.presenters.CommentHelper;

import androidx.appcompat.app.AppCompatActivity;

public class CommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        int newsId = getIntent().getIntExtra("id", 0);
        if (newsId == 0) {
            return;
        }

        CommentHelper commentHelper = new CommentHelper();
        new Thread(() -> {
            commentHelper.obtainAllLongComments(newsId);
            commentHelper.obtainAllShortComments(newsId);
            int failedComments = 0;
            for (Comment c : commentHelper.shortComments) {
                if (c.getReplyStatus() != 0) {
                    ++failedComments;
                }
            }
            System.out.println("" + failedComments);
            runOnUiThread(() -> {
                TextView info = findViewById(R.id.info);
                info.setText(String.format("Long:%d\nShort:%d"
                        , commentHelper.longComments.size(), commentHelper.shortComments.size()));
            });
        }).start();
    }
}
