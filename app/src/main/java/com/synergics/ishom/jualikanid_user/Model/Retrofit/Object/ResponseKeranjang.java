package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseKeranjang {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("total") public int total_price;
    @SerializedName("data") public ArrayList<Keranjang> data;

    public class Keranjang {
        @SerializedName("id") public String id;
        @SerializedName("fish_id") public String fish_id;
        @SerializedName("image") public String image;
        @SerializedName("name") public String name;
        @SerializedName("qty") public int qty;
        @SerializedName("price") public int price;
        @SerializedName("total_price") public int total_price;
    }
}
