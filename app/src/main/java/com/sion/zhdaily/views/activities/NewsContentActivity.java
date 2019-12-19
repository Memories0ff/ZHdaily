package com.sion.zhdaily.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.mvp.presenters.ContentPresenter;
import com.sion.zhdaily.mvp.views.IContentView;
import com.sion.zhdaily.tools.base.BaseFragmentActivity;
import com.sion.zhdaily.utils.beans.NewsSummary;
import com.sion.zhdaily.views.adapters.ContentPagerAdapter;

import java.util.ArrayList;

public class NewsContentActivity extends BaseFragmentActivity<IContentView, ContentPresenter> implements IContentView {

    //toolbar
    public Toolbar tbNewsContent = null;
    //评论按钮
    LinearLayout llCommentsBtn = null;
    public TextView tvCommentNum = null;
    //点赞按钮
    LinearLayout llThumbBtn = null;
    public ImageView ivThumb = null;
    public TextView tvPopularityNum = null;
    //分享按钮
    ImageView ivShareBtn = null;
    //收藏按钮
    ImageView ivCollectBtn = null;

    //左右滑动加载新闻ViewPager
    ViewPager vpContent = null;


    //---------------------------------Activity方法---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        //注册Connection Manager
        getPresenter().registerConnMgr();

        //获取控件实例
        tbNewsContent = findViewById(R.id.tb_newsContent);
        tbNewsContent.setNavigationOnClickListener((v) -> finish());

        llCommentsBtn = findViewById(R.id.ll_commentsBtn);
        tvCommentNum = findViewById(R.id.tv_commentsNum);

        llThumbBtn = findViewById(R.id.ll_thumbBtn);
        ivThumb = findViewById(R.id.iv_thumb);
        tvPopularityNum = findViewById(R.id.tv_popularityNum);

        ivShareBtn = findViewById(R.id.iv_newsContentShareBtn);
        ivCollectBtn = findViewById(R.id.iv_newsContentCollectBtn);

        vpContent = findViewById(R.id.vp_content);

        //设置点击事件
        llThumbBtn.setOnClickListener((v) -> {
            getPresenter().clickLike();
        });
        llCommentsBtn.setOnClickListener((v) -> {
            if (!getPresenter().isLoading()) {
                if (getPresenter().isNetworkConnected()) {
                    Intent commentsIntent = new Intent(NewsContentActivity.this, CommentsActivity.class);
                    commentsIntent.putExtra("newsId", getPresenter().getCurrentContent().getId());
                    commentsIntent.putExtra("longCommentNum", getPresenter().getCurrentContent().getLongComments());
                    commentsIntent.putExtra("shortCommentNum", getPresenter().getCurrentContent().getShortComments());
                    commentsIntent.putExtra("commentNum", getPresenter().getCurrentContent().getLongComments() + getPresenter().getCurrentContent().getShortComments());
                    startActivity(commentsIntent);
                } else {
                    Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivShareBtn.setOnClickListener((v) -> Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show());
        ivCollectBtn.setOnClickListener((v) -> Toast.makeText(this, "收藏", Toast.LENGTH_SHORT).show());

        //获取从上个activity传来到数据
        Intent intent = getIntent();
        getPresenter().setCurrentNewsId(intent.getIntExtra("id", 0));
        getPresenter().setIndex(intent.getIntExtra("index", 0));
        getPresenter().setNewsNum(intent.getIntExtra("newsNum", 0));
        getPresenter().setNewsSummaries((ArrayList<NewsSummary>) intent.getSerializableExtra("newsSummariesList"));


        ContentPagerAdapter adapter = new ContentPagerAdapter(getSupportFragmentManager(), this, vpContent, getPresenter().getIndex(), getPresenter().getNewsNum());
        vpContent.setAdapter(adapter);
        vpContent.setCurrentItem(getPresenter().getIndex(), false);


    }

    @Override
    protected void onDestroy() {
        getPresenter().unRegisterConnMgr();
        super.onDestroy();
    }


    //---------------------------抽象类方法实现---------------------------

    @Override
    protected IContentView createView() {
        return this;
    }

    @Override
    protected ContentPresenter createPresenter() {
        return new ContentPresenter(this);
    }

    //---------------------------IContentView接口方法---------------------------


    //点击点赞后更新界面
    @Override
    public void uiUpdateAfterLike(boolean isLikeAfterClick) {
        if (!isLikeAfterClick) {
            ivThumb.setImageResource(R.mipmap.thumb_up_white_48);
            tvPopularityNum.setText("" + (Integer.parseInt(tvPopularityNum.getText().toString()) - 1));
        } else {
            ivThumb.setImageResource(R.mipmap.thumb_up_orange);
            tvPopularityNum.setText("" + (Integer.parseInt(tvPopularityNum.getText().toString()) + 1));
        }
    }

    //设定切换Fragment并且未加载完毕时控件的显示
    @Override
    public void changeUIWhenSwitchFragment() {
        getPresenter().setLiked(false);
        ivThumb.setImageResource(R.mipmap.thumb_up_white_48);
        tvCommentNum.setText("...");
        tvPopularityNum.setText("...");
    }
}