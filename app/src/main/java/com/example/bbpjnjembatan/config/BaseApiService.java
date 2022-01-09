package com.example.bbpjnjembatan.config;

import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.example.bbpjnjembatan.roomDB.entity.User;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestDataJalan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestJenisKerusakan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestKategoriKerusakan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestStatus;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestTingkat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BaseApiService {
    // Login
    @FormUrlEncoded
    @POST("do_login")
    Call<User> loginRequest(
            @Field("nip") String nip,
            @Field("passwd") String passwd
    );

	// Tingkat Kerusakan
	@GET("tingkat")
	Call<RestTingkat> tingkatRequest();

	// Status Kerusakan
	@GET("status")
	Call<RestStatus> statusRequest();

	// Kategori Kerusakan
	@GET("kategori_kerusakan")
	Call<RestKategoriKerusakan> kategoriKerusakanRequest();

	// Jenis Kerusakan
	@FormUrlEncoded
	@POST("jenis_kerusakan")
	Call<RestJenisKerusakan> jenisKerusakanRequest(
			@Field("id_kategori") String id_kategori
	);

	@FormUrlEncoded
	@POST("tambah_PenilikJalan")
	Call<Pesan> addPenilikRequest(
			@Field("id_user") String id_user,
			@Field("id_kategori") String id_kategori,
			@Field("id_perbaikan") String id_perbaikan,
			@Field("id_tingkat") String id_tingkat,
			@Field("gambar_1") String gambar_1,
			@Field("gambar_2") String gambar_2,
			@Field("detail_kerusakan") String detail_kerusakan,
			@Field("lat") String lat,
			@Field("lng") String lng,
			@Field("id_satker") String id_satker,
			@Field("id_ppk") String id_ppk
	);

	// Data Jalan Satker
	@FormUrlEncoded
	@POST("penilikJalanMapsSatker")
	Call<RestDataJalan> dataJalanSatkerRequest(
			@Field("id_satker") String id_satker,
			@Field("limit") String limit,
			@Field("offset") String offset
	);

	@FormUrlEncoded
	@POST("penilikJalanMapsPpk")
	Call<RestDataJalan> dataJalanPpkRequest(
			@Field("id_ppk") String id_ppk,
			@Field("limit") String limit,
			@Field("offset") String offset
	);

	// Data Jalan Penilik Paging
	@FormUrlEncoded
	@POST("penilikJalanMapsPegawai")
	Call<RestDataJalan> dataJalanPegawaiRequest(
			@Field("id_user") String id_user,
			@Field("limit") String limit,
			@Field("offset") String offset
	);

	// Data Detail Jalan
	@FormUrlEncoded
	@POST("penilikJalanMapsId")
	Call<RestDataJalan> dataJalanDetailRequest(
			@Field("id_kerusakan") String id_kerusakan,
			@Field("limit") String limit,
			@Field("offset") String offset
	);

	// Data Jalan Penilik Paging
	@FormUrlEncoded
	@POST("penilikJalanMapsPegawai")
	Call<RestDataJalan> dataJalanPegawaiPagingRequest(
			@Field("id_user") String id_user,
			@Field("limit") Integer limit,
			@Field("offset") Integer offset
	);

	// Data Jalan PPK Paging
	@FormUrlEncoded
	@POST("penilikJalanMapsPpk")
	Call<RestDataJalan> dataJalanPpkPagingRequest(
			@Field("id_ppk") String id_ppk,
			@Field("limit") Integer limit,
			@Field("offset") Integer offset
	);

	// Data Jalan Satker Paging
	@FormUrlEncoded
	@POST("penilikJalanMapsSatker")
	Call<RestDataJalan> dataJalanSatkerPagingRequest(
			@Field("id_satker") String id_satker,
			@Field("limit") Integer limit,
			@Field("offset") Integer offset
	);

	// Update Data Jalan
	@FormUrlEncoded
	@POST("ubah_PenilikJalan")
	Call<Pesan> updatePenilikRequest(
			@Field("id_kerusakan") String id_kerusakan,
			@Field("id_user") String id_user,
			@Field("gambar1") String gambar1,
			@Field("gambar2") String gambar2,
			@Field("status") String status
	);

	// Ubah Foto
	@FormUrlEncoded
	@POST("update_Foto")
	Call<Pesan> ubahFotoRequest(
			@Field("id_user") String id_user,
			@Field("foto") String foto
	);

	// Data Pegawai
	@FormUrlEncoded
	@POST("data_Pegawai")
	Call<Pesan> dataRequest(
			@Field("id_user") String id_user
	);

	// Ubah Password
	@FormUrlEncoded
	@POST("ubah_Password")
	Call<Pesan> ubahPasswordRequest(
			@Field("id_user") String id_user,
			@Field("pw_lama") String pw_lama,
			@Field("pw_baru") String pw_baru
	);

	// Lupa Password
	@FormUrlEncoded
	@POST("do_forgot")
	Call<Pesan> lupaPasswordRequest(
			@Field("nip") String nip,
			@Field("email") String email
	);
}
