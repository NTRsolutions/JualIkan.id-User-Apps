package com.synergics.ishom.jualikanid_user.View.ImageSlide;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.R;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 5/21/17.
 */

public class AdapterImageViewPager extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    private ArrayList<ItemImageViewPager> mResources;

    public AdapterImageViewPager(Context context, ArrayList<ItemImageViewPager> mResources) {
        this.mResources = mResources;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_image_pager, container, false);

        ImageView thumbnail = (ImageView) itemView.findViewById(R.id.pagerItem);
//        thumbnail.setImageUrl(mResources.get(position), imageLoader);
        Picasso.with(mContext)
                .load(mResources.get(position).getUrl())
                .into(thumbnail);
        Log.d("Image Url ke : " + position+ " : ", mResources.get(position).getUrl());

        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Item ke : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
