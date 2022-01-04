package com.example.bbpjnjembatan.roomDB.entity.item;

import java.io.Serializable;

public class TingkatItem implements Serializable {
	String id_tingkat, nama_tingkat;

	public TingkatItem(String id_tingkat, String nama_tingkat) {
		this.id_tingkat = id_tingkat;
		this.nama_tingkat = nama_tingkat;
	}

	public String getId_tingkat() {
		return id_tingkat;
	}

	public void setId_tingkat(String id_tingkat) {
		this.id_tingkat = id_tingkat;
	}

	public String getNama_tingkat() {
		return nama_tingkat;
	}

	public void setNama_tingkat(String nama_tingkat) {
		this.nama_tingkat = nama_tingkat;
	}
}
