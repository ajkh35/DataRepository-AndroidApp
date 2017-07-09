package com.repositoryworks.datarepository.activityAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.repositoryworks.datarepository.R;

import butterknife.ButterKnife;

/**
 * Created by ajay3 on 7/4/2017.
 */

public class DrawerAdapter extends BaseAdapter {

    private Context mContext;
    String[] mArray;

    public DrawerAdapter(Context context){
        mContext = context;
        mArray = mContext.getResources().getStringArray(R.array.drawer_list);
    }

    @Override
    public int getCount() {
        return mArray.length;
    }

    @Override
    public Object getItem(int position) {
        return mArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_item,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mText.setText(mArray[position]);
        return convertView;
    }

    private class ViewHolder{

        TextView mText;

        ViewHolder(View view){
            mText = ButterKnife.findById(view,R.id.list_item_text);
        }
    }
}
