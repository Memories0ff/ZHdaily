package com.sion.zhdaily.views.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.sion.zhdaily.R;

public class NewsContentNestedScrollView extends NestedScrollView {

    Context mContext;

    public float appbarHeight;
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
        this.appbarHeight = getResources().getDimensionPixelOffset(R.dimen.news_content_appbar_height);
        oldX = 0;
        oldY = appbarHeight;
        setOnScrollChangeListener((OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (onScrolledListener != null) {
                onScrolledListener.move(oldScrollY, scrollY, scrollY - oldScrollY);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println(getY());
        onTopMovedListener.topMove(getY());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTopMovedListener.topMove(getY());
        return super.dispatchTouchEvent(ev);
    }

//    //用于下拉上滑时改变ToolBar的透明度
//    Thread uiChangeThread;
//    //是否执行线程内部的代码
//    boolean isUiChangeThreadAvailable = false;
//
//    //启动线程，activity执行onResume方法时用
//    public void startUiChangeThread() {
//        isUiChangeThreadAvailable = true;
//        uiChangeThread = new Thread(() -> {
//            while (true) {
//                try {
//                    //一秒30次循环
//                    Thread.sleep((long) (1000.0 / 30));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (!isUiChangeThreadAvailable) {
//                    break;
//                }
//                if (onTopMovedListener != null && getY() > 1) {
//                    //?????????可能存在问题
////                    onTopMovedListener.topMove(getY());
//                }
//            }
//        });
//        uiChangeThread.start();
//    }
//
//    //停止线程，在activity执行onPause方法时用
//    public void stopUiChangeThread() {
//        isUiChangeThreadAvailable = false;
//    }

    //接口
    @FunctionalInterface
    public interface OnTopMovedListener {
        void topMove(float currentY);
    }

    OnTopMovedListener onTopMovedListener = null;

    public interface OnScrolledListener {
        void move(float oldY, float currentY, float dy);
    }

    OnScrolledListener onScrolledListener = null;

    public void setOnTopMovedListener(OnTopMovedListener onTopMovedListener) {
        this.onTopMovedListener = onTopMovedListener;
    }

    public void setOnScrolledListener(OnScrolledListener onScrolledListener) {
        this.onScrolledListener = onScrolledListener;
    }

}
