package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseHome {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public Data data;

    public class Data {
        @SerializedName("promo") public ArrayList<Promo> promos;
        @SerializedName("fish_cat") public ArrayList<FishCat> fishCats;

        public class FishCat {
            @SerializedName("fish_category_id") public String fish_category_id;
            @SerializedName("fish_category_name") public String fish_category_name;
            @SerializedName("fish_item") public ArrayList<Fish> fishes;

            public class Fish {
                @SerializedName("fish_id") public String fish_id;
                @SerializedName("fish_image") public String fish_image;
                @SerializedName("fish_name") public String fish_name;
                @SerializedName("fish_price") public String fish_price;
                @SerializedName("fish_distribution_location") public String fish_distribution_location;
                @SerializedName("fish_category_id") public String fish_category_id;
                @SerializedName("fish_condition_id") public String fish_condition_id;
                @SerializedName("fish_size_id") public String fish_size_id;
                @SerializedName("fish_stock") public String fish_stock;
                @SerializedName("fish_rating") public int fish_rating;
                @SerializedName("fish_total_rating") public int fish_total_rating;
                @SerializedName("fish_description") public String fish_description;
                @SerializedName("fish_date") public String fish_date;
            }
        }

        public class Promo {
            @SerializedName("promo_id") public String promo_id;
            @SerializedName("promo_name") public String promo_name;
            @SerializedName("promo_start") public String promo_start;
            @SerializedName("promo_end") public String promo_end;
            @SerializedName("promo_image") public String promo_image;
        }
    }
}
