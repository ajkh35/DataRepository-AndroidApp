package com.repositoryworks.datarepository.fragmentAdapters;

import android.content.Context;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.models.MusicModel;
import com.repositoryworks.datarepository.utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by ajay3 on 7/5/2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MusicModel> mList;

    public MusicAdapter(Context context) {
        mContext = context;
        mList = generateList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == Constants.ITEM_TYPE_PROGRESS){
            view = LayoutInflater.from(mContext).inflate(R.layout.progress_bar,parent,false);
            return new ProgressViewHolder(view);
        }else if(viewType == Constants.ITEM_TYPE_VIEW){
            view = LayoutInflater.from(mContext).inflate(R.layout.music_list_item,parent,false);
            return new MusicViewHolder(view);
        }else{ return null; }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MusicViewHolder){
            ((MusicViewHolder) holder).mTitle.setText(mList.get(position).getTitle());
            ((MusicViewHolder) holder).mAlbum.setText(mList.get(position).getAlbum());
            ((MusicViewHolder) holder).mArtistAndYear.setText("by "+mList.get(position).getArtist()
                    +" in "+String.valueOf(mList.get(position).getYear()));
            ((MusicViewHolder) holder).mYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,mList.get(holder.getAdapterPosition()).getYoutubeID(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            ((MusicViewHolder) holder).mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Card #"+(holder.getAdapterPosition()+1),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).mProgress.setIndeterminate(true);
        }else{ Toast.makeText(mContext,"View creation error.",Toast.LENGTH_SHORT).show();}
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position) == null){
            return Constants.ITEM_TYPE_PROGRESS;
        }else {
            return Constants.ITEM_TYPE_VIEW;
        }
    }

    /**
     * ViewHolder class for songs
     */
    private class MusicViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitle;
        private TextView mAlbum;
        private TextView mArtistAndYear;
        private Button mYoutube;
        private RelativeLayout mCard;

        MusicViewHolder(View itemView) {
            super(itemView);
            mTitle = ButterKnife.findById(itemView,R.id.music_title);
            mAlbum = ButterKnife.findById(itemView,R.id.music_album);
            mArtistAndYear = ButterKnife.findById(itemView,R.id.music_by_artist_in_year);
            mYoutube = ButterKnife.findById(itemView,R.id.music_youtube);
            mCard = ButterKnife.findById(itemView,R.id.music_card);
        }
    }

    /**
     * ViewHolder class for progressbar
     */
    private class ProgressViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar mProgress;

        ProgressViewHolder(View itemView) {
            super(itemView);
            mProgress = ButterKnife.findById(itemView, R.id.loading);
        }
    }

    /**
     * Temporary data list generator
     * @return
     */
    private ArrayList<MusicModel> generateList(){
        ArrayList<MusicModel> list = new ArrayList<>();
        for(int i=1;i<11;i++){
            list.add(new MusicModel("Title #"+i,"Artist #"+i,"Album #"+i,i,"ID"));
        }
        return list;
    }
}