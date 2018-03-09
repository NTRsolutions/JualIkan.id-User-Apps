package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseLogin {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public Data data;

    public static class Data {

        public Data() {}

        @SerializedName("user_id") public String user_id;
        @SerializedName("user_full_name") public String user_full_name;
        @SerializedName("user_image") public String user_image;
        @SerializedName("user_phone") public String user_phone;
        @SerializedName("user_email") public String user_email;
        @SerializedName("user_password") public String user_password;
        @SerializedName("user_device_id") public String user_device_id;
        @SerializedName("user_kota_id") public String user_kota_id;
        @SerializedName("user_address") public String user_address;
        @SerializedName("user_saldo") public String user_saldo;
    }


}
