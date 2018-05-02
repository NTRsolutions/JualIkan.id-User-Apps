package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseOrderProcessed {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public ArrayList<OrderProcessed> data;

    public class OrderProcessed {
        @SerializedName("orderId") public String orderId;
        @SerializedName("orderIdNumber") public String orderIdNumber;
        @SerializedName("orderAdress") public String orderAdress;
        @SerializedName("orderTotal") public int orderTotal;
        @SerializedName("orderDate") public String orderDate;
        @SerializedName("orderStatus") public String orderStatus;
    }
}
