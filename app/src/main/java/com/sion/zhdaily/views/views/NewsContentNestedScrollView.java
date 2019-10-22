package com.sion.zhdaily.views.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.sion.zhdaily.R;

public class NewsContentNestedScrollView extends NestedScrollView {

    Context mContext;

    public float transRange;
    float oldX, oldY;
    float currentX, currentY;

    public NewsContentNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public NewsContentNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsContentNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.transRange = getResources().getDimensionPixelOffset(R.dimen.news_content_top_bar_height);
        oldX = 0;
        oldY = transRange;
        setOnScrollChangeListener((OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (onNewsContentNestedScrollSlidedListener != null) {
                onNewsContentNestedScrollSlidedListener.move(oldScrollY, scrollY, scrollY - oldScrollY);
            }
        });

        uiChangeThread = new Thread(() -> {
            while (true) {
                try {
                    //一秒30次循环
                    Thread.sleep((long) (1000.0 / 30));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isUiChangeThreadAvailable) {
                    break;
                }
                if (onNewsContentNestedScrollViewTopMovedListener != null && getY() > 1) {
                    //?????????可能存在问题
                    onNewsContentNestedScrollViewTopMovedListener.topMove(getY(), transRange);
                }
            }
        });
    }

    //用于下拉时改变ToolBar的透明度
    Thread uiChangeThread;
    //是否执行线程内部的代码
    boolean isUiChangeThreadAvailable = false;

    //启动线程，activity执行onResume方法时用
    public void startUiChangeThread() {
        isUiChangeThreadAvailable = true;
        uiChangeThread.start();
    }

    //停止线程，在activity执行onPause方法时用
    public void stopUiChangeThread() {
        isUiChangeThreadAvailable = false;
    }

    //接口
    @FunctionalInterface
    public interface OnNewsContentNestedScrollViewTopMovedListener {
        void topMove(float currentY, float transRange);
    }

    OnNewsContentNestedScrollViewTopMovedListener onNewsContentNestedScrollViewTopMovedListener = null;

    public interface OnNewsContentNestedScrollSlidedListener {
        void move(float oldY, float currentY, float dy);
    }

    OnNewsContentNestedScrollSlidedListener onNewsContentNestedScrollSlidedListener = null;

    public void setOnNewsContentNestedScrollViewTopMovedListener(OnNewsContentNestedScrollViewTopMovedListener onNewsContentNestedScrollViewTopMovedListener) {
        this.onNewsContentNestedScrollViewTopMovedListener = onNewsContentNestedScrollViewTopMovedListener;
    }

    public void setOnNewsContentNestedScrollSlidedListener(OnNewsContentNestedScrollSlidedListener onNewsContentNestedScrollSlidedListener) {
        this.onNewsContentNestedScrollSlidedListener = onNewsContentNestedScrollSlidedListener;
    }

}
