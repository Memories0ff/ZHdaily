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

//    enum ITEM_TYPE {
//        //新闻
//        SUMMARIES_ITEM,
//        //日期
//        DATE_ITEM,
//        //头条
//        TOP_ITEM
//    }

    //统计已经加载的DateItem数
//    private int dateItemNum = 0;


    //从属的Activity
    public MainActivity mainActivity;
    //对应的RecyclerView
    public RecyclerView mRv;
    //内容列表
    public List<NewsSummary> mContents;

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == ITEM_TYPE.SUMMARIES_ITEM.ordinal()) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.rv_summary_item_view, parent, false);
        return new NewsSummaryViewHolder(view);
//        } else if (viewType == ITEM_TYPE.DATE_ITEM.ordinal()) {
//            View view = LayoutInflater.from(mainActivity).inflate(R.layout.rv_date_item_view, parent, false);
//            return new DateItemViewHolder(view);
//        } else if (viewType == ITEM_TYPE.TOP_ITEM.ordinal()) {
//            View view = LayoutInflater.from(mainActivity).inflate(R.layout.rv_top_item_view, parent, false);
//            return new TopNewsSummaryViewHolder(view);
//        }
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        int type = getItemViewType(position);
//        int newsSummaryPosition = position - 1 - mainActivity.helper.loadedNewsSummaryDays;
//        if (type == ITEM_TYPE.SUMMARIES_ITEM.ordinal()) {
        TextView textView = ((NewsSummaryViewHolder) holder).getTvNewsTitle();
        textView.setText(mContents.get(position).getTitle());

        ImageView imageView = ((NewsSummaryViewHolder) holder).getIvNewsTitlePic();
        Glide.with(mainActivity).load(mContents.get(position).getImageUrl()).into(imageView);

        View itemView = ((NewsSummaryViewHolder) holder).getItemView();
        itemView.setOnClickListener((v) -> Toast.makeText(mainActivity, mContents.get(position).getTitle(), Toast.LENGTH_SHORT).show());

//        } else if (type == ITEM_TYPE.DATE_ITEM.ordinal()) {
//            //DateItem数+1
////            ++dateItemNum;
//            TextView textView = ((DateItemViewHolder) holder).getTvDate();
//            textView.setText("xx月xx日 星期x");
//        } else if (type == ITEM_TYPE.TOP_ITEM.ordinal()) {
//            //加载顶部内容
//        }
        //??????????????????????????√
    }

    @Override
    public int getItemCount() {
        //?????????????????????????
        return mContents == null ? 0 : mContents.size();
//        int count = (mContents.size() + 1 + mainActivity.helper.loadedNewsSummaryDays);
//        return mContents == null ? 0 : count;
    }

//    @Override
//    public int getItemViewType(int position) {
//        //???????????????????????????
//        if (position == 0) {
//            return ITEM_TYPE.TOP_ITEM.ordinal();
//        } else if (position == 1 || position == 1 + mContents.size() + mainActivity.helper.loadedNewsSummaryDays) {
//            return ITEM_TYPE.DATE_ITEM.ordinal();
//        } else {
//            return ITEM_TYPE.SUMMARIES_ITEM.ordinal();
//        }
//    }

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

    class DateItemViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView tvDate;

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }

        public TextView getTvDate() {
            return tvDate;
        }

        public void setTvDate(TextView tvDate) {
            this.tvDate = tvDate;
        }

        public DateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    class TopNewsSummaryViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView ivTopPic;

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }

        public ImageView getIvTopPic() {
            return ivTopPic;
        }

        public void setIvTopPic(ImageView ivTopPic) {
            this.ivTopPic = ivTopPic;
        }

        public TopNewsSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivTopPic = itemView.findViewById(R.id.iv_topPic);
        }
    }
}
