package com.sion.zhdaily.views.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sion.zhdaily.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class NewsContentNestedScrollView extends NestedScrollView {

    Context mContext;

    float transRange;
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
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                currentX = v.getX();
                currentY = v.getY();
                float dx = currentX - oldX;
                float dy = currentY - oldY;

                if (currentY > 0.01) {
                    if (onNewsContentNestedScrollViewTopMovedListener != null) {
                        onNewsContentNestedScrollViewTopMovedListener.topMove(oldY, currentY, dy, transRange);
                    }
                }

                oldX = currentX;
                oldY = currentY;
            }
            performClick();
            return false;
        });
        setOnScrollChangeListener((OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (onNewsContentNestedScrollSlidedListener != null) {
                onNewsContentNestedScrollSlidedListener.move(oldScrollY, scrollY, scrollY - oldScrollY);
            }
        });
    }

    @FunctionalInterface
    public interface OnNewsContentNestedScrollViewTopMovedListener {
        void topMove(float oldY, float currentY, float dy, float transRange);
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
