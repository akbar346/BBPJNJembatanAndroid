package com.example.bbpjnjembatan.roomDB.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_user")
public class User implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String id_user;
	private String nama_lengkap;
	private String email;
    private String no_hp;
    private String nip;
	private String id_jabatan;
	private String nama_jabatan;
	private String id_ppk;
	private String kode_ppk;
	private String nama_ppk;
	private String id_satker;
	private String kode_satker;
	private String nama_satker;
	private boolean success;
	private String message;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@NonNull
	public String getId_user() {
		return id_user;
	}

	public void setId_user(@NonNull String id_user) {
		this.id_user = id_user;
	}

	public String getNama_lengkap() {
		return nama_lengkap;
	}

	public void setNama_lengkap(String nama_lengkap) {
		this.nama_lengkap = nama_lengkap;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNo_hp() {
		return no_hp;
	}

	public void setNo_hp(String no_hp) {
		this.no_hp = no_hp;
	}

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	public String getId_jabatan() {
		return id_jabatan;
	}

	public void setId_jabatan(String id_jabatan) {
		this.id_jabatan = id_jabatan;
	}

	public String getNama_jabatan() {
		return nama_jabatan;
	}

	public void setNama_jabatan(String nama_jabatan) {
		this.nama_jabatan = nama_jabatan;
	}

	public String getId_ppk() {
		return id_ppk;
	}

	public void setId_ppk(String id_ppk) {
		this.id_ppk = id_ppk;
	}

	public String getKode_ppk() {
		return kode_ppk;
	}

	public void setKode_ppk(String kode_ppk) {
		this.kode_ppk = kode_ppk;
	}

	public String getNama_ppk() {
		return nama_ppk;
	}

	public void setNama_ppk(String nama_ppk) {
		this.nama_ppk = nama_ppk;
	}

	public String getId_satker() {
		return id_satker;
	}

	public void setId_satker(String id_satker) {
		this.id_satker = id_satker;
	}

	public String getKode_satker() {
		return kode_satker;
	}

	public void setKode_satker(String kode_satker) {
		this.kode_satker = kode_satker;
	}

	public String getNama_satker() {
		return nama_satker;
	}

	public void setNama_satker(String nama_satker) {
		this.nama_satker = nama_satker;
	}
}
