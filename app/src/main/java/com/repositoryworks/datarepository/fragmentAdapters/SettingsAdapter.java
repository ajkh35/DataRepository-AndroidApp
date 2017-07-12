package com.repositoryworks.datarepository.fragmentAdapters;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.repositoryworks.datarepository.R;

import butterknife.ButterKnife;

/**
 * Created by ajay3 on 7/11/2017.
 */

public class SettingsAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mList;
    private int NOTIFICATION_VIEW = 1;

    /**
     * Adapter constructor
     * @param context Activity context
     */
    public SettingsAdapter(Context context){
        mContext = context;
        mList = context.getResources().getStringArray(R.array.settings_list);
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int position) {
        return mList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.settings_list_item,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mText.setText(mList[position]);
        if(getItemViewType(position)==NOTIFICATION_VIEW){
            holder.mSwitch.setVisibility(View.VISIBLE);
        }else{
            holder.mSwitch.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(mList[position].equals(mContext.getString(R.string.notification))){
            return NOTIFICATION_VIEW;
        }else{
            return 0;
        }
    }

    /**
     * ViewHolder class
     */
    private class ViewHolder {

        private TextView mText;
        private SwitchCompat mSwitch;
        private LinearLayout mLayout;

        ViewHolder(View view){
            mText = ButterKnife.findById(view,R.id.setting_title);
            mSwitch = ButterKnife.findById(view,R.id.notification_switch);
            mLayout = ButterKnife.findById(view,R.id.settings_layout);
        }
    }
}