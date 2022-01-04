package com.example.bbpjnjembatan.roomDB.entity.item;

import java.io.Serializable;

public class StatusItem implements Serializable {
	String id_status, nama_status;

	public StatusItem(String id_status, String nama_status) {
		this.id_status = id_status;
		this.nama_status = nama_status;
	}

	public String getId_status() {
		return id_status;
	}

	public void setId_status(String id_status) {
		this.id_status = id_status;
	}

	public String getNama_status() {
		return nama_status;
	}

	public void setNama_status(String nama_status) {
		this.nama_status = nama_status;
	}
}
