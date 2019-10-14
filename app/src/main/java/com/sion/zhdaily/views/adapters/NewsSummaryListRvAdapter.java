package com.sion.zhdaily.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sion.zhdaily.R;
import com.sion.zhdaily.models.beans.NewsSummary;
import com.sion.zhdaily.views.activities.MainActivity;

import java.util.List;

public class NewsSummaryListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    enum ITEM_TYPE {
        HEADER, NORMAL
    }

    //从属的Activity
    public MainActivity mainActivity;
    //对应的RecyclerView
    public RecyclerView mRv;
    //数据源
    public List<NewsSummary> mContents;
    //列表头部
    View mHeaderView = null;

    public View getmHeaderView() {
        return mHeaderView;
    }

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
    }


    //是否正在加载内容
    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public NewsSummaryListRvAdapter(MainActivity mainActivity, RecyclerView mRv, List<NewsSummary> mContents) {
        this.mainActivity = mainActivity;
        this.mRv = mRv;
        this.mContents = mContents;
    }

    public NewsSummaryListRvAdapter(MainActivity mainActivity, RecyclerView mRv, List<NewsSummary> mContents, View headerView) {
        this.mainActivity = mainActivity;
        this.mRv = mRv;
        this.mContents = mContents;
        this.mHeaderView = headerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.HEADER.ordinal()) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            View view = LayoutInflater.from(mainActivity).inflate(R.layout.rv_summary_item_view, parent, false);
            return new NewsSummaryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE.NORMAL.ordinal()) {
            int realPosition = getRealPosition(position);
            NewsSummaryViewHolder newsSummaryViewHolder = (NewsSummaryViewHolder) holder;
            TextView textViewNewsTitle = newsSummaryViewHolder.getTvNewsTitle();
            textViewNewsTitle.setText(mContents.get(realPosition).getTitle());

            ImageView imageView = newsSummaryViewHolder.getIvNewsTitlePic();
            Glide.with(mainActivity).load(mContents.get(realPosition).getImageUrl()).into(imageView);

            View clickableView = newsSummaryViewHolder.getClickableView();
            clickableView.setOnClickListener((v) -> Toast.makeText(mainActivity, mContents.get(realPosition).getTitle(), Toast.LENGTH_SHORT).show());
            //某天的第一条新闻要显示日期
            if (mContents.get(realPosition).isFirstNewsSummary()) {
                newsSummaryViewHolder.getTvNewsDate().setVisibility(View.VISIBLE);
                newsSummaryViewHolder.getTvNewsDate().setText(mContents.get(realPosition).getDateStr());
            }
            //移出的View会直接重用，所以移出的显示日期的View在重用之后也还会显示之前的日期，要在这种情况下设置不显示
            else {
                newsSummaryViewHolder.getTvNewsDate().setVisibility(View.GONE);
                newsSummaryViewHolder.getTvNewsDate().setText(null);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return ITEM_TYPE.NORMAL.ordinal();
        } else {
            if (position == 0) {
                return ITEM_TYPE.HEADER.ordinal();
            } else {
                return ITEM_TYPE.NORMAL.ordinal();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            if (mContents == null) {
                return 0;
            } else {
                return mContents.size();
            }
        } else {
            if (mContents == null) {
                return 1;
            } else {
                return mContents.size() + 1;
            }
        }
    }

    public void notifyNewsSummaryItemInserted(int positionStart, int itemCount) {
        int startOffset = mHeaderView == null ? 0 : 1;
        notifyItemRangeInserted(positionStart + startOffset, itemCount);
    }

    public int getRealPosition(int position) {
        if (mHeaderView == null) {
            return position;
        } else {
            return position - 1;
        }
    }

    class NewsSummaryViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView tvNewsTitle;
        private ImageView ivNewsTitlePic;
        private TextView tvNewsDate;
        private View clickableView;

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

        public TextView getTvNewsDate() {
            return tvNewsDate;
        }

        public void setTvNewsDate(TextView tvNewsDate) {
            this.tvNewsDate = tvNewsDate;
        }

        public void setIvNewsTitlePic(ImageView ivNewsTitlePic) {
            this.ivNewsTitlePic = ivNewsTitlePic;
        }

        public View getClickableView() {
            return clickableView;
        }

        public void setClickableView(View clickableView) {
            this.clickableView = clickableView;
        }

        public NewsSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvNewsTitle = itemView.findViewById(R.id.tv_newsTitle);
            this.ivNewsTitlePic = itemView.findViewById(R.id.iv_newsTitlePic);
            this.tvNewsDate = itemView.findViewById(R.id.tv_newsDate);
            this.clickableView = itemView.findViewById(R.id.clickable_view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private View itemView;

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
