package com.sion.zhdaily.views.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.CommentHelper;

public class CommentsActivity extends AppCompatActivity {

    public int newsId;
    public int commentNum;
    public int longCommentNum;
    public int shortCommentNum;

    Toolbar tbComments = null;
    LinearLayout llWriteCommentBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        newsId = getIntent().getIntExtra("newsId", 0);
        if (newsId == 0) {
            return;
        }
        commentNum = getIntent().getIntExtra("commentNum", 0);
        longCommentNum = getIntent().getIntExtra("longCommentNum", 0);
        shortCommentNum = getIntent().getIntExtra("shortCommentNum", 0);

        tbComments = findViewById(R.id.tb_comments);
        tbComments.setTitle(commentNum + "条点评");
        tbComments.setNavigationOnClickListener((v) -> finish());
        llWriteCommentBtn = findViewById(R.id.ll_writeCommentBtn);
        llWriteCommentBtn.setOnClickListener((v) -> Toast.makeText(this, "写点评", Toast.LENGTH_SHORT).show());

        CommentHelper commentHelper = new CommentHelper();
        new Thread(() -> {
            commentHelper.obtainAllLongComments(newsId);
            commentHelper.obtainAllShortComments(newsId);
            runOnUiThread(() -> {
                TextView info = findViewById(R.id.info);
                info.setText(String.format("Long:%d\nShort:%d"
                        , commentHelper.longComments.size(), commentHelper.shortComments.size()));
            });
        }).start();
    }
}
