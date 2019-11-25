package com.sion.zhdaily.views.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sion.zhdaily.R;

public class NewsSummaryItemDecoration extends RecyclerView.ItemDecoration {

    DecorationCallback callback;
    Context context;
    TextPaint textPaint;
    int topGap;


    public NewsSummaryItemDecoration(DecorationCallback callback, Context context) {
        this.callback = callback;
        this.context = context;

        Resources res = context.getResources();

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(res.getDimensionPixelOffset(R.dimen.NewsSummarySectionTitleFontSize));

        topGap = res.getDimensionPixelOffset(R.dimen.NewsSummarySectionTopGap);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0 && callback.isFirstInGroup(pos)) {
            outRect.top = topGap;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(childView);
            if (pos != 0 && callback.isFirstInGroup(pos)) {
//                int top = childView.getTop() - topGap;
                int bottom = childView.getTop() - topGap / 2;
                int left = parent.getLeft() + (int) (topGap * (2.0 / 3));
//                int right = parent.getRight() - topGap / 2;
                String text = callback.getTitleText(pos);
                c.drawText(text, left, bottom, textPaint);
            }
        }
    }

    public interface DecorationCallback {
        boolean isFirstInGroup(int pos);

        String getTitleText(int pos);
    }
}
