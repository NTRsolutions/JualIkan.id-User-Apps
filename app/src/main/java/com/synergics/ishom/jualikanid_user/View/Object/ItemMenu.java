package com.synergics.ishom.jualikanid_user.View.Object;

import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseHome;

/**
 * Created by asmarasusanto on 1/21/18.
 */

public class ItemMenu {
    private int id;
    private int size;
    private ResponseHome.Data.FishCat.Fish fish;

    public ItemMenu(int id, int size, ResponseHome.Data.FishCat.Fish fish) {
        this.id = id;
        this.size = size;
        this.fish = fish;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public ResponseHome.Data.FishCat.Fish getFish() {
        return fish;
    }
}
