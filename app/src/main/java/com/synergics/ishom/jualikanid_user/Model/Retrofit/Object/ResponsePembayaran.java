package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponsePembayaran {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("statusOrder") public boolean statusOrder;
    @SerializedName("statusMessage") public String messageOrder;
    @SerializedName("cart") public Keranjang cart;
    @SerializedName("user") public Profile user;
    @SerializedName("delivery_time") public List<DeliveryTime> deliveryTimes;
    @SerializedName("payment_type") public List<PaymentType> paymentTypes;
    @SerializedName("delviery_cost_pkm") public int delviery_cost_pkm;

    public class Keranjang {
        @SerializedName("items") public List<Items> items;
        @SerializedName("total") public int total;
        @SerializedName("koperasi") public Koperasi koperasi;
    }

    public class Profile {
        @SerializedName("user_id") public String user_id;
        @SerializedName("user_full_name") public String user_full_name;
        @SerializedName("user_image") public String user_image;
        @SerializedName("user_saldo") public String user_saldo;
        @SerializedName("check") public boolean check;
    }

    public class DeliveryTime{
        @SerializedName("delivery_time_id") public String delivery_time_id;
        @SerializedName("delivery_time_name") public String delivery_time_name;
        @SerializedName("delivery_time_start") public String delivery_time_start;
        @SerializedName("delivery_time_end") public String delivery_time_end;
        @SerializedName("status") public int status;
        @SerializedName("message") public String message;

//        private RadioButton radioButton = null;
        private int cheked = 0;

        public int getCheked() {
            return cheked;
        }

        public void setCheked(int cheked) {
            this.cheked = cheked;
        }

//        public RadioButton getRadioButton() {
//            return radioButton;
//        }
//
//        public void setRadioButton(RadioButton radioButton) {
//            this.radioButton = radioButton;
//        }
    }

    public class PaymentType{
        @SerializedName("payment_type_id") public String payment_type_id;
        @SerializedName("payment_type_name") public String payment_type_name;
    }

    public class Koperasi {
        @SerializedName("id") public String id;
        @SerializedName("nama") public String nama;
        @SerializedName("lat") public String lat;
        @SerializedName("lng") public String lng;
    }

    public class Items {
        @SerializedName("id") public String id;
        @SerializedName("fish_id") public String fish_id;
        @SerializedName("image") public String image;
        @SerializedName("name") public String name;
        @SerializedName("qty") public int qty;
        @SerializedName("price") public int price;
        @SerializedName("total_price") public int total_price;
    }
}
