package com.synergics.ishom.jualikanid_user.View.Object;

import android.widget.CheckBox;

/**
 * Created by asmarasusanto on 3/11/18.
 */

public class DelivTime {
    private int id;
    private String nama;
    private String caption;
    private int check = 0;
    private CheckBox checkBox;
    private int status;
    private String message;

    public DelivTime(int id, String nama, String caption, String message, int status) {
        this.id = id;
        this.nama = nama;
        this.caption = caption;
        this.status = status;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
