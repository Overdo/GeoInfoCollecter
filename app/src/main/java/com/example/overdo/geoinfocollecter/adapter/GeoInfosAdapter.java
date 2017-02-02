package com.example.overdo.geoinfocollecter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.App;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;

import java.util.List;

/**
 * Created by Overdo on 2017/2/2.
 */

public class GeoInfosAdapter extends BaseAdapter {

    private IOnItemClickListener mItemClickListener;
    private List<GeoInfo> mGeoInfoList;

    public GeoInfosAdapter(List<GeoInfo> list, IOnItemClickListener listener) {
        this.mItemClickListener = listener;
        this.mGeoInfoList = list;
    }

    @Override
    public int getCount() {
        return mGeoInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGeoInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        viewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(App.getContext()).inflate(R.layout.item_geoinfoslist, null);
            holder = new GeoInfosAdapter.viewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.point_code);
            holder.latlng = (TextView) convertView.findViewById(R.id.point_latlng);
            holder.elevation = (TextView) convertView.findViewById(R.id.point_elevation);
            holder.root = (LinearLayout) convertView.findViewById(R.id.root);
            convertView.setTag(holder);
        } else {
            holder = (GeoInfosAdapter.viewHolder) convertView.getTag();
        }

        holder.code.setText( mGeoInfoList.get(position).getCode());
        holder.latlng.setText( mGeoInfoList.get(position).getLongtitude() + "/" +
                mGeoInfoList.get(position).getLatitude() );
        holder.elevation.setText(mGeoInfoList.get(position).getElevation());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onclick(position);
            }
        });

        return convertView;
    }

    class viewHolder {
        TextView code, latlng, elevation;
        LinearLayout root;
    }

}
