package com.example.karthi.retrofit2_example.Adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.karthi.retrofit2_example.R;
import com.example.karthi.retrofit2_example.Response.GitHubUserResponse;

import java.util.List;


public class ListviewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    public static List<GitHubUserResponse> data;
    private LayoutInflater inflater;

    public ListviewAdapter(Context mContext, List<GitHubUserResponse> data) {
        this.data = data;
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i).getId();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview, null);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.film_id);
            holder.name = (TextView) convertView.findViewById(R.id.film_name);
//              holder.obj= (ImageView) convertView.findViewById(R.id.film_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.id.setText(data.get(position).getId());
        holder.name.setText(data.get(position).getTitle());

      /*  Glide
                .with(mContext)
                .load(data.get(position).getImage())
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.obj);*/

        return convertView;
    }

    static class ViewHolder {
        //        ImageView obj;
        TextView id, name;


    }

}
