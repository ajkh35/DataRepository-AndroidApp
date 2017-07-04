package com.repositoryworks.datarepository.fragmentAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.models.HomeModel;
import com.repositoryworks.datarepository.utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by ajay3 on 7/3/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<HomeModel> mList;

    public HomeAdapter(Context context){
        mContext = context;
        mList = generateList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == Constants.ITEM_TYPE_VIEW){
            view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item,parent,false);
            return new FeedViewHolder(view);
        }else if(viewType == Constants.ITEM_TYPE_PROGRESS){
            view = LayoutInflater.from(mContext).inflate(R.layout.progress_bar,parent,false);
            return new ProgressViewHolder(view);
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FeedViewHolder){
            ((FeedViewHolder) holder).mTitle.setText(mList.get(position).getTitle());
            ((FeedViewHolder) holder).mCredits.setText(mList.get(position).getCredits());
            ((FeedViewHolder) holder).mRelease.setText(mList.get(position).getRelease());
            ((FeedViewHolder) holder).mYoutubeID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,mList.get(holder.getAdapterPosition()).getYoutubeID(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            ((FeedViewHolder) holder).mHomeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Card #"+(holder.getAdapterPosition()+1),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).mProgress.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position)== null){
            return Constants.ITEM_TYPE_PROGRESS;
        }else {
            return Constants.ITEM_TYPE_VIEW;
        }
    }

    private class FeedViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitle;
        private TextView mCredits;
        private TextView mRelease;
        private TextView mYoutubeID;
        private RelativeLayout mHomeCard;

        FeedViewHolder(View itemView) {
            super(itemView);
            mTitle = ButterKnife.findById(itemView,R.id.home_card_title);
            mCredits = ButterKnife.findById(itemView,R.id.home_card_credits);
            mRelease = ButterKnife.findById(itemView,R.id.home_card_release);
            mYoutubeID = ButterKnife.findById(itemView,R.id.home_card_youtube);
            mHomeCard = ButterKnife.findById(itemView,R.id.home_card);
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar mProgress;

        ProgressViewHolder(View itemView) {
            super(itemView);
            mProgress = ButterKnife.findById(itemView,R.id.loading);
        }
    }

    /**
     * Temporary method to simulate data list
     * @return
     */
    private ArrayList<HomeModel> generateList(){
        ArrayList<HomeModel> list = new ArrayList<>();

        for(int i=1;i<=20;i++){
            HomeModel model = new HomeModel();
            model.setTitle("Title #"+i);
            model.setCredits("Guy #"+i);
            model.setRelease("Year #"+i);
            model.setYoutubeID("ID");
            list.add(model);
        }

        return list;
    }
}