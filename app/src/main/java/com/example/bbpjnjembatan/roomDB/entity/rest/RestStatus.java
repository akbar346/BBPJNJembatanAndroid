package com.example.bbpjnjembatan.roomDB.entity.rest;

import com.example.bbpjnjembatan.roomDB.entity.item.StatusItem;

import java.io.Serializable;
import java.util.List;

public class RestStatus implements Serializable {
	private List<StatusItem> hasil;
	private String success, message;

	public List<StatusItem> getHasil() {
		return hasil;
	}

	public void setHasil(List<StatusItem> hasil) {
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
