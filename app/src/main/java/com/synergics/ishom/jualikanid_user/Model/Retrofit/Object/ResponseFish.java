package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseFish {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public Fish data;

    public class Fish {
        @SerializedName("fish_id") public String fish_id;
        @SerializedName("fish_image") public String fish_image;
        @SerializedName("fish_name") public String fish_name;
        @SerializedName("fish_price") public String fish_price;
        @SerializedName("fish_distribution_location") public String fish_distribution_location;
        @SerializedName("fish_category_id") public String fish_category_id;
        @SerializedName("fish_condition_id") public String fish_condition_id;
        @SerializedName("fish_koperasi") public Koperasi fish_koperasi;
        @SerializedName("fish_size_id") public String fish_size_id;
        @SerializedName("fish_stock") public String fish_stock;
        @SerializedName("fish_rating") public int fish_rating;
        @SerializedName("fish_total_rating") public int fish_total_rating;
        @SerializedName("fish_description") public String fish_description;
        @SerializedName("fish_date") public String fish_date;
        @SerializedName("fish_review") public List<Review> fish_review;
    }

    public class Koperasi {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("image") public String image;
        @SerializedName("status") public String status;
    }

    public class Review {
        @SerializedName("user_full_name") public String user_full_name;
        @SerializedName("review_jumalh") public String review_jumalh;
        @SerializedName("user_image") public String user_image;
        @SerializedName("review_text") public String review_text;
    }

}
