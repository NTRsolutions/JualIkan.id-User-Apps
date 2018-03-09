package com.synergics.ishom.jualikanid_user.Model.Retrofit.Object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseKategori {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public ArrayList<ResponseHome.Data.FishCat.Fish> data;
}
