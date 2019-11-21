package com.sion.zhdaily.views.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sion.zhdaily.views.activities.MainActivity;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;

public class NewsSummaryListRecyclerView extends RecyclerView {

    //按下y位置，放开y位置，按下减放开的差值
    float downY, upY, moveDY;


    public NewsSummaryListRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NewsSummaryListRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsSummaryListRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //解决下拉刷新过程中自动继续加载的问题，以及放开手指时才开始继续加载
                if (moveDY >= 0 || newState != 0) {
                    return;
                }

                //不再加载以及滚动到底部时进行加载操作
                if (((MainActivity) recyclerView.getContext()).isNetworkConnected()) {
                    if (!((NewsSummaryListRvAdapter) getAdapter()).isLoading() && !recyclerView.canScrollVertically(1)) {
                        NewsSummaryListRvAdapter adapter = (NewsSummaryListRvAdapter) getAdapter();
                        //设置为正在加载，防止加载时多次上滑造成重复加载
                        adapter.setLoading(true);
                        //在新线程中进行加载操作
                        new Thread(() -> {
                            MainActivity mainActivity = adapter.mainActivity;
                            //一次加载三天内容
                            mainActivity.helper.getNewsSummariesDayByDay();
                            mainActivity.runOnUiThread(() -> adapter.notifyNewsSummaryItemInserted(mainActivity.helper.insertRangeStartPosition, mainActivity.helper.loadedNewsSummaryNum));
                            mainActivity.helper.getNewsSummariesDayByDay();
                            mainActivity.runOnUiThread(() -> adapter.notifyNewsSummaryItemInserted(mainActivity.helper.insertRangeStartPosition, mainActivity.helper.loadedNewsSummaryNum));
                            mainActivity.helper.getNewsSummariesDayByDay();
                            //加载完成，设置为不在加载
                            mainActivity.runOnUiThread(() -> {
                                adapter.notifyNewsSummaryItemInserted(mainActivity.helper.insertRangeStartPosition, mainActivity.helper.loadedNewsSummaryNum);
                                adapter.setLoading(false);
                            });
                        }).start();
                    }
                } else {
                    Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (visibleItemMoveToTopListener != null) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                    visibleItemMoveToTopListener.visibleItemMoveToTop(linearLayoutManager.findFirstVisibleItemPosition());
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                upY = ev.getRawY();
                moveDY = upY - downY;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    //监听RecyclerView的Item滑动到RecyclerView顶部时触发事件（即当Item成为RecyclerView中第一个可视视图时触发事件）
    @FunctionalInterface
    public interface OnVisibleItemMoveToTopListener {
        void visibleItemMoveToTop(int pos);
    }

    private OnVisibleItemMoveToTopListener visibleItemMoveToTopListener;

//    public OnVisibleItemMoveToTopListener getVisibleItemMoveToTopListener() {
//        return visibleItemMoveToTopListener;
//    }

    public void setVisibleItemMoveToTopListener(OnVisibleItemMoveToTopListener visibleItemMoveToTopListener) {
        this.visibleItemMoveToTopListener = visibleItemMoveToTopListener;
    }
}