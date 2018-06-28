package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseReview {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("fish") public Fish fish;
    @SerializedName("review") public Review review;

    public class Fish {
        @SerializedName("fish_id") public String fish_id;
        @SerializedName("fish_image") public String fish_image;
        @SerializedName("fish_name") public String fish_name;
        @SerializedName("fish_price") public String fish_price;
        @SerializedName("fish_koperasi_id") public String fish_category_id;
    }

    public class Review {
        @SerializedName("review_jumalh") public String review_jumalh;
        @SerializedName("review_text") public String review_text;
    }

}
