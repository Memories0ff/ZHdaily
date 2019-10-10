package com.sion.zhdaily.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sion.zhdaily.R;

import java.util.List;

public class NewsSummaryListRvAdapter extends RecyclerView.Adapter<NewsSummaryListRvAdapter.NewsSummaryViewHolder> {

    public Context mContext;
    public RecyclerView mRv;
    public List<String> mContents;

    public NewsSummaryListRvAdapter(Context mContext, RecyclerView mRv, List<String> mContents) {
        this.mContext = mContext;
        this.mRv = mRv;
        this.mContents = mContents;
    }

    @NonNull
    @Override
    public NewsSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_view, parent, false);
        return new NewsSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsSummaryViewHolder holder, int position) {
        TextView textView = holder.getTvNewsTitle();
        textView.setText(mContents.get(position));
        RecyclerView.LayoutParams itemViewLP = (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
        LinearLayout.LayoutParams rvLP = (LinearLayout.LayoutParams) mRv.getLayoutParams();
        rvLP.height = itemViewLP.height * getItemCount();
        mRv.setLayoutParams(rvLP);
    }

    @Override
    public int getItemCount() {
        return mContents.size();
    }

    class NewsSummaryViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView tvNewsTitle;
        private ImageView ivNewsTitlePic;

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }

        public TextView getTvNewsTitle() {
            return tvNewsTitle;
        }

        public void setTvNewsTitle(TextView tvNewsTitle) {
            this.tvNewsTitle = tvNewsTitle;
        }

        public ImageView getIvNewsTitlePic() {
            return ivNewsTitlePic;
        }

        public void setIvNewsTitlePic(ImageView ivNewsTitlePic) {
            this.ivNewsTitlePic = ivNewsTitlePic;
        }

        public NewsSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvNewsTitle = itemView.findViewById(R.id.tv_newsTitle);
            this.ivNewsTitlePic = itemView.findViewById(R.id.iv_newsTitlePic);
        }
    }
}
