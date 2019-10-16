package com.sion.zhdaily.views.views;

import android.content.Context;
import android.util.AttributeSet;

import com.sion.zhdaily.views.activities.MainActivity;
import com.sion.zhdaily.views.adapters.NewsSummaryListRvAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewsSummaryListRecyclerView extends RecyclerView {

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
                //不在加载以及滚动到底部时进行加载操作
                if (!((NewsSummaryListRvAdapter) getAdapter()).isLoading() && !recyclerView.canScrollVertically(1)) {
                    NewsSummaryListRvAdapter adapter = (NewsSummaryListRvAdapter) getAdapter();
                    //设置为正在加载，防止加载时多次上滑造成重复加载
                    adapter.setLoading(true);
                    //在新线程中进行加载操作
                    new Thread(() -> {
                        MainActivity mainActivity = adapter.mainActivity;
                        mainActivity.helper.getNewsSummariesDayByDay();
//                      Toast.makeText(mainActivity, "加载完成", Toast.LENGTH_SHORT).show();
                        //加载完成，设置为不在加载
                        mainActivity.runOnUiThread(() -> {
//                            adapter.notifyDataSetChanged();
                            adapter.notifyNewsSummaryItemInserted(mainActivity.helper.insertRangeStartPosition, mainActivity.helper.loadedNewsSummaryNum);
                            adapter.setLoading(false);
                        });
                    }).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                if (visibleItemMoveToTopListener != null) {
                    visibleItemMoveToTopListener.visibleItemMoveToTop(linearLayoutManager.findFirstVisibleItemPosition());
                }

            }
        });
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
