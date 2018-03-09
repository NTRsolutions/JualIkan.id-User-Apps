package com.synergics.ishom.jualikanid_user.View.ImageSlide;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseFish;
import com.synergics.ishom.jualikanid_user.R;

import java.util.List;

/**
 * Created by asmarasusanto on 5/21/17.
 */

public class AdapterReviewPager extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    private List<ResponseFish.Review> mResources;

    public AdapterReviewPager(Context context, List<ResponseFish.Review> mResources) {
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
        View itemView = mLayoutInflater.inflate(R.layout.layout_review_pager, container, false);

        ImageView profile = (ImageView) itemView.findViewById(R.id.userImage);
        TextView nama = (TextView) itemView.findViewById(R.id.nama);
        TextView pesanReview = (TextView) itemView.findViewById(R.id.pesanReview);
        RatingBar rating = (RatingBar) itemView.findViewById(R.id.ratingReview);

        Picasso.with(mContext)
                .load(AppConfig.url + mResources.get(position).user_image)
                .into(profile);

        nama.setText(mResources.get(position).user_full_name);
        rating.setRating(Float.parseFloat(mResources.get(position).review_jumalh));

        pesanReview.setText(mResources.get(position).review_text);
//        Toast.makeText(mContext, mResources.get(position).review_text + pesanReview.getFontFeatureSettings() , Toast.LENGTH_SHORT).show();
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
