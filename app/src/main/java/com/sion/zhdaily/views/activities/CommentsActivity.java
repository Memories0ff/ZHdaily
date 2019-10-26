package com.sion.zhdaily.views.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.presenters.CommentHelper;
import com.sion.zhdaily.views.adapters.CommentRvAdapter;
import com.sion.zhdaily.views.views.CommentRecyclerView;

public class CommentsActivity extends AppCompatActivity {

    public int newsId;
    public int commentNum;
    public int longCommentNum;
    public int shortCommentNum;

    Toolbar tbComments = null;
    LinearLayout llWriteCommentBtn = null;

    CommentHelper mHelper = new CommentHelper();
    CommentRecyclerView rvComments = null;
    CommentRvAdapter mCommentAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        newsId = getIntent().getIntExtra("newsId", 0);
        commentNum = getIntent().getIntExtra("commentNum", 0);
        longCommentNum = getIntent().getIntExtra("longCommentNum", 0);
        shortCommentNum = getIntent().getIntExtra("shortCommentNum", 0);

        tbComments = findViewById(R.id.tb_comments);
        tbComments.setTitle(commentNum + "条点评");
        tbComments.setNavigationOnClickListener((v) -> finish());
        llWriteCommentBtn = findViewById(R.id.ll_writeCommentBtn);
        llWriteCommentBtn.setOnClickListener((v) -> Toast.makeText(this, "写点评", Toast.LENGTH_SHORT).show());

        rvComments = findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        mCommentAdapter = new CommentRvAdapter(this, rvComments, mHelper);
        rvComments.setAdapter(mCommentAdapter);

        new Thread(() -> {
            mHelper.obtainAllLongComments(newsId);
            mHelper.obtainShortCommentsByStep(newsId);
            runOnUiThread(() -> mCommentAdapter.notifyDataSetChanged());
        }).start();

    }
}
