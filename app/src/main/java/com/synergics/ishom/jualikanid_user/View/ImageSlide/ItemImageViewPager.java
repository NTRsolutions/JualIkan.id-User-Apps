package com.synergics.ishom.jualikanid_user.View.ImageSlide;

/**
 * Created by asmarasusanto on 9/20/17.
 */

public class ItemImageViewPager {
    private String id;
    private String name;
    private String url;

    public ItemImageViewPager(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
