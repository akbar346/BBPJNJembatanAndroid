package com.example.bbpjnjembatan.roomDB.entity.rest;

import com.example.bbpjnjembatan.roomDB.entity.item.JenisKerusakanItem;

import java.io.Serializable;
import java.util.List;

public class RestJenisKerusakan implements Serializable {
    private List<JenisKerusakanItem> hasil;
    private String success, message;

    public List<JenisKerusakanItem> getHasil() {
        return hasil;
    }

    public void setHasil(List<JenisKerusakanItem> hasil) {
        this.hasil = hasil;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
