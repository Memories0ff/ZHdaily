package com.sion.zhdaily.views.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.mvp.presenters.CommentsPresenter;
import com.sion.zhdaily.mvp.views.ICommentsView;
import com.sion.zhdaily.utils.base.BaseActivity;
import com.sion.zhdaily.views.adapters.CommentRvAdapter;
import com.sion.zhdaily.views.views.CommentRecyclerView;

public class CommentsActivity extends BaseActivity<ICommentsView, CommentsPresenter> implements ICommentsView {

    //加载等待Dialog
    ProgressDialog pdLoading = null;

    Toolbar tbComments = null;
    LinearLayout llWriteCommentBtn = null;

    CommentRecyclerView rvComments = null;
    CommentRvAdapter mCommentAdapter = null;

    @Override
    protected ICommentsView createView() {
        return this;
    }

    @Override
    protected CommentsPresenter createPresenter() {
        return new CommentsPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getPresenter().registerConnMgr();


        //从上个Activity获取信息
        int longNum = getIntent().getIntExtra("longCommentNum", 0);
        int shortNum = getIntent().getIntExtra("shortCommentNum", 0);
        int commentNum = getIntent().getIntExtra("commentNum", 0);
        getPresenter().initCommentsData(getIntent().getIntExtra("newsId", 0), longNum, shortNum, this);

        //设置界面
        tbComments = findViewById(R.id.tb_comments);
        tbComments.setTitle(commentNum + "条点评");
        tbComments.setNavigationOnClickListener((v) -> finish());
        llWriteCommentBtn = findViewById(R.id.ll_writeCommentBtn);
        llWriteCommentBtn.setOnClickListener((v) -> Toast.makeText(this, "写点评", Toast.LENGTH_SHORT).show());

        rvComments = findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCommentAdapter = new CommentRvAdapter(this, rvComments);
        rvComments.setAdapter(mCommentAdapter);

        pdLoading = new ProgressDialog(this);
        pdLoading.setIndeterminate(true);
        pdLoading.setMessage("正在加载");
        pdLoading.setCancelable(false);
        pdLoading.show();

        getPresenter().loadInitialComments();
    }

    @Override
    protected void onDestroy() {
        getPresenter().unregisterConnMgr();
        super.onDestroy();
    }

    @Override
    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uiChangeWhenInitialLoadingFinish() {
        mCommentAdapter.notifyCommentSetChanged();
        pdLoading.dismiss();
    }


    @Override
    public void uiChangeWhenContinueLoadingFinish() {
        mCommentAdapter.notifyItemRangeInserted(2 + Math.max(1, getPresenter().getLongComments().size()) + getPresenter().getShortComments().size(), getPresenter().getCurrentLoadedShortComments());
        getPresenter().setLoading(false);

    }
}