package com.example.overdo.geoinfocollecter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;

import java.util.List;

public class GeoInfoAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<GeoInfo> mGeoInfos;
    private IOnItemClickListener mClickListener;

    public GeoInfoAdapter(Context mContext, List<GeoInfo> geoInfos, IOnItemClickListener listener) {
        this.mContext = mContext;
        this.mGeoInfos = geoInfos;
        this.mClickListener = listener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_geoinfoslist, null);
        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView code = (TextView) convertView.findViewById(R.id.point_code);
        TextView latlng = (TextView) convertView.findViewById(R.id.point_latlng);
        TextView elevation = (TextView) convertView.findViewById(R.id.point_elevation);

        code.setText(mGeoInfos.get(position).getCode());
        latlng.setText("(" + mGeoInfos.get(position).getLatitude() + "," + mGeoInfos.get(position).getLongtitude() + ")");
        elevation.setText(mGeoInfos.get(position).getElevation());

        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_normal);
        Button deleteBtn = (Button) convertView.findViewById(R.id.delete);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onclick(position);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.ondelete(position);
                swipeLayout.close();
            }
        });
    }

    @Override
    public int getCount() {
        return mGeoInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
