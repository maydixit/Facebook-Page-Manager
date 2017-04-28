package com.example.may.facebook_page_manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by May on 4/1/17.
 * List adapter class to display multiple items with formatting
 */

public class CustomListAdapter extends BaseAdapter {

    private ArrayList<ListItem> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<ListItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ListItem getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.messageView = (TextView) convertView.findViewById(R.id.message_chars);
            holder.timeView = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageView.setText(listData.get(position).getMessage_chars());
        holder.timeView.setText("At: " + listData.get(position).getTime());

        return convertView;
    }

    static class ViewHolder {
        TextView messageView;
        TextView timeView;
    }


    public void add(ListItem item){
        listData.add(item);
        notifyDataSetChanged();
        Log.d("ADAPTER", "add called ");
    }

    public void clear(){
        listData = new ArrayList<ListItem>();
    }
}
