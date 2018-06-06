package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseTrackingOrder {

    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public OrderDetail data;

    public class OrderDetail {

        @SerializedName("cart") public Cart cart;
        @SerializedName("orderLocation") public OrderLocation orderLocation;
        @SerializedName("payment") public Payment payment;
        @SerializedName("driver") public Driver driver;
        @SerializedName("koperasi") public Koperasi koperasi;
        @SerializedName("status") public int status;
        @SerializedName("status_txt") public String statusText;
    }

    public class Driver {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("phone") public String phone;
        @SerializedName("image") public String image;
        @SerializedName("device_id") public String device_id;
    }

    public class Koperasi {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("address") public String address;
        @SerializedName("lat") public String lat;
        @SerializedName("lng") public String lng;
    }

    public class Cart {
        @SerializedName("items") public List<CartItem> items;
        @SerializedName("total") public int total;
    }

    public class CartItem {
        @SerializedName("id") public String id;
        @SerializedName("fish_id") public String fish_id;
        @SerializedName("image") public String image;
        @SerializedName("name") public String name;
        @SerializedName("qty") public int qty;
        @SerializedName("price") public int price;
        @SerializedName("total_price") public int total_price;
    }

    public class OrderLocation {
        @SerializedName("address") public String address;
        @SerializedName("lat") public String lat;
        @SerializedName("lng") public String lng;
    }

    public class Payment {
        @SerializedName("cart") public int cart;
        @SerializedName("delivery") public int delivery;
        @SerializedName("total") public int total;
        @SerializedName("url") public String url;
    }
}
