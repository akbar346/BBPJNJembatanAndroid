package com.example.bbpjnjembatan.roomDB.entity.item;

import java.io.Serializable;

public class JenisKerusakanItem implements Serializable {
    private String id_perbaikan, kode_kerusakan, id_kategori, nama_kerusakan, ket_perbaikan;

	public JenisKerusakanItem(String id_perbaikan, String kode_kerusakan, String id_kategori, String nama_kerusakan, String ket_perbaikan) {
		this.id_perbaikan = id_perbaikan;
		this.kode_kerusakan = kode_kerusakan;
		this.id_kategori = id_kategori;
		this.nama_kerusakan = nama_kerusakan;
		this.ket_perbaikan = ket_perbaikan;
	}

	public String getId_perbaikan() {
		return id_perbaikan;
	}

	public void setId_perbaikan(String id_perbaikan) {
		this.id_perbaikan = id_perbaikan;
	}

	public String getKode_kerusakan() {
		return kode_kerusakan;
	}

	public void setKode_kerusakan(String kode_kerusakan) {
		this.kode_kerusakan = kode_kerusakan;
	}

	public String getId_kategori() {
		return id_kategori;
	}

	public void setId_kategori(String id_kategori) {
		this.id_kategori = id_kategori;
	}

	public String getNama_kerusakan() {
		return nama_kerusakan;
	}

	public void setNama_kerusakan(String nama_kerusakan) {
		this.nama_kerusakan = nama_kerusakan;
	}

	public String getKet_perbaikan() {
		return ket_perbaikan;
	}

	public void setKet_perbaikan(String ket_perbaikan) {
		this.ket_perbaikan = ket_perbaikan;
	}
}
