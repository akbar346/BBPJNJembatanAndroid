package com.example.bbpjnjembatan.roomDB.entity.rest;

import com.example.bbpjnjembatan.roomDB.entity.item.DataJalanItems;
import com.example.bbpjnjembatan.roomDB.entity.item.JenisKerusakanItem;

import java.io.Serializable;
import java.util.List;

public class RestDataJalan implements Serializable {
	private List<DataJalanItems> hasil;
	private String success, message;

	public List<DataJalanItems> getHasil() {
		return hasil;
	}

	public void setHasil(List<DataJalanItems> hasil) {
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
