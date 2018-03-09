package com.synergics.ishom.jualikanid_user.View.Spinner;

/**
 * Created by asmarasusanto on 11/10/17.
 */

public class SpinnerItem {

    private int id;
    private String title;

    public SpinnerItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
