package com.example.bbpjnjembatan.roomDB.entity.item;

import java.io.Serializable;

public class KategoriKerusakanItem implements Serializable {
    private String id_kategori, kode_kategori, nama_kategori, variabel, img;

    public KategoriKerusakanItem(String id_kategori, String kode_kategori, String nama_kategori, String variabel, String img) {
        this.id_kategori = id_kategori;
        this.kode_kategori = kode_kategori;
        this.nama_kategori = nama_kategori;
        this.variabel = variabel;
        this.img = img;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getKode_kategori() {
        return kode_kategori;
    }

    public void setKode_kategori(String kode_kategori) {
        this.kode_kategori = kode_kategori;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public void setNama_kategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }

    public String getVariabel() {
        return variabel;
    }

    public void setVariabel(String variabel) {
        this.variabel = variabel;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
