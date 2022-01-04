package com.example.bbpjnjembatan.roomDB.entity.item;

import java.io.Serializable;

public class DataJalanItems implements Serializable {
	String id_kerusakan;
	String id_input;
	String nip_input;
	String nama_input;
	String hp_input;
	String jabatan_input;
	String id_kategori;
	String kode_kategori;
	String nama_kategori;
	String id_perbaikan;
	String kode_kerusakan;
	String nama_kerusakan;
	String ket_perbaikan;
	String marker;
	String gambar_1;
	String gambar_2;
	String tgl_pengecekan;
	String detail_kerusakan;
	String id_user_proses;
	String nip_proses;
	String nama_proses;
	String hp_proses;
	String jabatan_proses;
	String gambar_proses_1;
	String gambar_proses_2;
	String tgl_proses;
	String id_user_selesai;
	String nip_selesai;
	String nama_selesai;
	String hp_selesai;
	String jabatan_selesai;
	String gambar_selesai_1;
	String gambar_selesai_2;
	String tgl_selesai;
	String id_status;
	String nama_status;
	String id_tingkat;
	String nama_tingkat;
	String id_satker;
	String kode_satker;
	String nama_satker;
	String id_ppk;
	String kode_ppk;
	String nama_ppk;
	String lat;
	String lng;
	private String type;
	private boolean selected = false;

	public DataJalanItems(String type) {
		this.type = type;
	}

	public String getId_kerusakan() {
		return id_kerusakan;
	}

	public void setId_kerusakan(String id_kerusakan) {
		this.id_kerusakan = id_kerusakan;
	}

	public String getId_input() {
		return id_input;
	}

	public void setId_input(String id_input) {
		this.id_input = id_input;
	}

	public String getNip_input() {
		return nip_input;
	}

	public void setNip_input(String nip_input) {
		this.nip_input = nip_input;
	}

	public String getNama_input() {
		return nama_input;
	}

	public void setNama_input(String nama_input) {
		this.nama_input = nama_input;
	}

	public String getHp_input() {
		return hp_input;
	}

	public void setHp_input(String hp_input) {
		this.hp_input = hp_input;
	}

	public String getJabatan_input() {
		return jabatan_input;
	}

	public void setJabatan_input(String jabatan_input) {
		this.jabatan_input = jabatan_input;
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

	public String getGambar_1() {
		return gambar_1;
	}

	public void setGambar_1(String gambar_1) {
		this.gambar_1 = gambar_1;
	}

	public String getGambar_2() {
		return gambar_2;
	}

	public void setGambar_2(String gambar_2) {
		this.gambar_2 = gambar_2;
	}

	public String getTgl_pengecekan() {
		return tgl_pengecekan;
	}

	public void setTgl_pengecekan(String tgl_pengecekan) {
		this.tgl_pengecekan = tgl_pengecekan;
	}

	public String getDetail_kerusakan() {
		return detail_kerusakan;
	}

	public void setDetail_kerusakan(String detail_kerusakan) {
		this.detail_kerusakan = detail_kerusakan;
	}

	public String getId_user_proses() {
		return id_user_proses;
	}

	public void setId_user_proses(String id_user_proses) {
		this.id_user_proses = id_user_proses;
	}

	public String getNip_proses() {
		return nip_proses;
	}

	public void setNip_proses(String nip_proses) {
		this.nip_proses = nip_proses;
	}

	public String getNama_proses() {
		return nama_proses;
	}

	public void setNama_proses(String nama_proses) {
		this.nama_proses = nama_proses;
	}

	public String getHp_proses() {
		return hp_proses;
	}

	public void setHp_proses(String hp_proses) {
		this.hp_proses = hp_proses;
	}

	public String getJabatan_proses() {
		return jabatan_proses;
	}

	public void setJabatan_proses(String jabatan_proses) {
		this.jabatan_proses = jabatan_proses;
	}

	public String getGambar_proses_1() {
		return gambar_proses_1;
	}

	public void setGambar_proses_1(String gambar_proses_1) {
		this.gambar_proses_1 = gambar_proses_1;
	}

	public String getGambar_proses_2() {
		return gambar_proses_2;
	}

	public void setGambar_proses_2(String gambar_proses_2) {
		this.gambar_proses_2 = gambar_proses_2;
	}

	public String getTgl_proses() {
		return tgl_proses;
	}

	public void setTgl_proses(String tgl_proses) {
		this.tgl_proses = tgl_proses;
	}

	public String getId_user_selesai() {
		return id_user_selesai;
	}

	public void setId_user_selesai(String id_user_selesai) {
		this.id_user_selesai = id_user_selesai;
	}

	public String getNip_selesai() {
		return nip_selesai;
	}

	public void setNip_selesai(String nip_selesai) {
		this.nip_selesai = nip_selesai;
	}

	public String getNama_selesai() {
		return nama_selesai;
	}

	public void setNama_selesai(String nama_selesai) {
		this.nama_selesai = nama_selesai;
	}

	public String getHp_selesai() {
		return hp_selesai;
	}

	public void setHp_selesai(String hp_selesai) {
		this.hp_selesai = hp_selesai;
	}

	public String getJabatan_selesai() {
		return jabatan_selesai;
	}

	public void setJabatan_selesai(String jabatan_selesai) {
		this.jabatan_selesai = jabatan_selesai;
	}

	public String getGambar_selesai_1() {
		return gambar_selesai_1;
	}

	public void setGambar_selesai_1(String gambar_selesai_1) {
		this.gambar_selesai_1 = gambar_selesai_1;
	}

	public String getGambar_selesai_2() {
		return gambar_selesai_2;
	}

	public void setGambar_selesai_2(String gambar_selesai_2) {
		this.gambar_selesai_2 = gambar_selesai_2;
	}

	public String getTgl_selesai() {
		return tgl_selesai;
	}

	public void setTgl_selesai(String tgl_selesai) {
		this.tgl_selesai = tgl_selesai;
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

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString(){
		return
				"HasilItem{" +
						"id_kerusakan = '" + id_kerusakan + '\'' +
						",id_input = '" + id_input + '\'' +
						",nip_input = '" + nip_input + '\'' +
						",nama_input = '" + nama_input + '\'' +
						",hp_input = '" + hp_input + '\'' +
						",jabatan_input = '" + jabatan_input + '\'' +
						",id_kategori = '" + id_kategori + '\'' +
						",kode_kategori = '" + kode_kategori + '\'' +
						",nama_kategori = '" + nama_kategori + '\'' +
						",id_perbaikan = '" + id_perbaikan + '\'' +
						",kode_kerusakan = '" + kode_kerusakan + '\'' +
						",nama_kerusakan = '" + nama_kerusakan + '\'' +
						",ket_perbaikan = '" + ket_perbaikan + '\'' +
						",marker = '" + marker + '\'' +
						",gambar_1 = '" + gambar_1 + '\'' +
						",gambar_2 = '" + gambar_2 + '\'' +
						",tgl_pengecekan = '" + tgl_pengecekan + '\'' +
						",detail_kerusakan = '" + detail_kerusakan + '\'' +
						",id_user_proses = '" + id_user_proses + '\'' +
						",nip_proses = '" + nip_proses + '\'' +
						",nama_proses = '" + nama_proses + '\'' +
						",hp_proses = '" + hp_proses + '\'' +
						",jabatan_proses = '" + jabatan_proses + '\'' +
						",gambar_proses_1 = '" + gambar_proses_1 + '\'' +
						",gambar_proses_2 = '" + gambar_proses_2 + '\'' +
						",tgl_proses = '" + tgl_proses + '\'' +
						",id_user_selesai = '" + id_user_selesai + '\'' +
						",nip_selesai = '" + nip_selesai + '\'' +
						",nama_selesai = '" + nama_selesai + '\'' +
						",hp_selesai = '" + hp_selesai + '\'' +
						",jabatan_selesai = '" + jabatan_selesai + '\'' +
						",gambar_selesai_1 = '" + gambar_selesai_1 + '\'' +
						",gambar_selesai_2 = '" + gambar_selesai_2 + '\'' +
						",tgl_selesai = '" + tgl_selesai + '\'' +
						",id_status = '" + id_status + '\'' +
						",nama_status = '" + nama_status + '\'' +
						",id_tingkat = '" + id_tingkat + '\'' +
						",nama_tingkat = '" + nama_tingkat + '\'' +
						",id_satker = '" + id_satker + '\'' +
						",kode_satker = '" + kode_satker + '\'' +
						",nama_satker = '" + nama_satker + '\'' +
						",id_ppk = '" + id_ppk + '\'' +
						",kode_ppk = '" + kode_ppk + '\'' +
						",nama_ppk = '" + nama_ppk + '\'' +
						",lat = '" + lat + '\'' +
						",lng = '" + lng + '\'' +
						"}";
	}
}
