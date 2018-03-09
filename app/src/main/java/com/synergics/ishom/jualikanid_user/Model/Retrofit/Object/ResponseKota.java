package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseKota {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public ArrayList<Data> data;

    public class Data {
        @SerializedName("kota_id") public String kota_id;
        @SerializedName("kota_provinsi_id") public String kota_provinsi_id;
        @SerializedName("kota_name") public String kota_name;
    }
}
