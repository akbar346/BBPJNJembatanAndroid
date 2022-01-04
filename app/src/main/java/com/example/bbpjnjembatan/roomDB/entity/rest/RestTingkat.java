package com.example.bbpjnjembatan.roomDB.entity.rest;

import com.example.bbpjnjembatan.roomDB.entity.item.TingkatItem;

import java.io.Serializable;
import java.util.List;

public class RestTingkat implements Serializable {
	private List<TingkatItem> hasil;
	private String success, message;

	public List<TingkatItem> getHasil() {
		return hasil;
	}

	public void setHasil(List<TingkatItem> hasil) {
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
