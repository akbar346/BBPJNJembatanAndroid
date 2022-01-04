package com.example.bbpjnjembatan.roomDB.entity.rest;

import com.example.bbpjnjembatan.roomDB.entity.item.KategoriKerusakanItem;

import java.io.Serializable;
import java.util.List;

public class RestKategoriKerusakan implements Serializable {
    private List<KategoriKerusakanItem> hasil;
    private String success, message;

    public List<KategoriKerusakanItem> getHasil() {
        return hasil;
    }

    public void setHasil(List<KategoriKerusakanItem> hasil) {
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
