package com.sion.zhdaily.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sion.zhdaily.R;
import com.sion.zhdaily.helpers.CommentHelper;
import com.sion.zhdaily.views.adapters.CommentRvAdapter;
import com.sion.zhdaily.views.views.CommentRecyclerView;

public class CommentsActivity extends AppCompatActivity {

    public int newsId;
    public int commentNum;
    public int longCommentNum;
    public int shortCommentNum;

    //网络状态
    private boolean isNetworkConnected = false;

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    private ConnectivityManager connMgr = null;
    private NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();

    //加载等待Dialog
    ProgressDialog pdLoading = null;

    Toolbar tbComments = null;
    LinearLayout llWriteCommentBtn = null;

    CommentHelper mHelper;
    CommentRecyclerView rvComments = null;
    CommentRvAdapter mCommentAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //网络状态
        connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connMgr.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isNetworkConnected = true;
        }

        //从上个Activity获取信息
        newsId = getIntent().getIntExtra("newsId", 0);
        commentNum = getIntent().getIntExtra("commentNum", 0);
        longCommentNum = getIntent().getIntExtra("longCommentNum", 0);
        shortCommentNum = getIntent().getIntExtra("shortCommentNum", 0);

        mHelper = new CommentHelper(newsId, longCommentNum, shortCommentNum, this);

        //设置界面
        tbComments = findViewById(R.id.tb_comments);
        tbComments.setTitle(commentNum + "条点评");
        tbComments.setNavigationOnClickListener((v) -> finish());
        llWriteCommentBtn = findViewById(R.id.ll_writeCommentBtn);
        llWriteCommentBtn.setOnClickListener((v) -> Toast.makeText(this, "写点评", Toast.LENGTH_SHORT).show());

        rvComments = findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCommentAdapter = new CommentRvAdapter(this, rvComments, mHelper);
        rvComments.setAdapter(mCommentAdapter);

        pdLoading = new ProgressDialog(this);
        pdLoading.setIndeterminate(true);
        pdLoading.setMessage("正在加载");
        pdLoading.setCancelable(false);
        pdLoading.show();

        if (isNetworkConnected()) {
            new Thread(() -> {
                mHelper.obtainAllLongComments();
                runOnUiThread(() -> {
                    mCommentAdapter.notifyCommentSetChanged();
                    pdLoading.dismiss();
                });
            }).start();
        } else {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connMgr.unregisterNetworkCallback(networkCallback);
    }

    class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isNetworkConnected = false;
        }
    }
}