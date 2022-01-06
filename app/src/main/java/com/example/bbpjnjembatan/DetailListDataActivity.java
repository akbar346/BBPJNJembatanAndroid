package com.example.bbpjnjembatan;

import static android.view.View.GONE;

import static com.example.bbpjnjembatan.DialogPreview.dialogPreview;
import static com.example.bbpjnjembatan.config.UtilsApi.BASE_IMG_API;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestDataJalan;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailListDataActivity extends AppCompatActivity {
	@BindView(R.id.rl_pb)
	RelativeLayout rl_pb;
	@BindView(R.id.rl_detail)
	RelativeLayout rl_detail;
	@BindView(R.id.gambar1rusak)
	ImageView gambar1rusak;
	@BindView(R.id.gambar2rusak)
	ImageView gambar2rusak;
	@BindView(R.id.gambar1baik)
	ImageView gambar1baik;
	@BindView(R.id.gambar2baik)
	ImageView gambar2baik;
	@BindView(R.id.ll_penanganan)
	LinearLayout ll_penanganan;
	@BindView(R.id.ll_nama)
	LinearLayout ll_nama;
	@BindView(R.id.ll_txtBaik)
	LinearLayout ll_txtBaik;
	@BindView(R.id.ll_tglBaik)
	LinearLayout ll_tglBaik;
	@BindView(R.id.ll_imgBaik)
	LinearLayout ll_imgBaik;
	@BindView(R.id.txt_nama)
	TextView txt_nama;
	@BindView(R.id.txt_status)
	TextView txt_status;
	@BindView(R.id.txt_satker)
	TextView txt_satker;
	@BindView(R.id.txt_ppk)
	TextView txt_ppk;
	@BindView(R.id.txt_tanggalRusak)
	TextView txt_tanggalRusak;
	@BindView(R.id.txt_tanggalBaik)
	TextView txt_tanggalBaik;
	@BindView(R.id.txt_kategori)
	TextView txt_kategori;
	@BindView(R.id.txt_jenis)
	TextView txt_jenis;
	@BindView(R.id.txt_penangan)
	TextView txt_penangan;
	@BindView(R.id.txt_tingkat)
	TextView txt_tingkat;
	@BindView(R.id.txt_lat)
	TextView txt_lat;
	@BindView(R.id.txt_lng)
	TextView txt_lng;
	@BindView(R.id.txt_ket)
	TextView txt_ket;
	@BindView(R.id.ll_txtProses)
	LinearLayout ll_txtProses;
	@BindView(R.id.ll_imgProses)
	LinearLayout ll_imgProses;
	@BindView(R.id.gambar1proses)
	ImageView gambar1proses;
	@BindView(R.id.gambar2proses)
	ImageView gambar2proses;
	@BindView(R.id.ll_tglProses)
	LinearLayout ll_tglProses;
	@BindView(R.id.txt_tanggalProses)
	TextView txt_tanggalProses;
	@BindView(R.id.ll_namaProses)
	LinearLayout ll_namaProses;
	@BindView(R.id.txt_namaProses)
	TextView txt_namaProses;
	@BindView(R.id.ll_namaBaik)
	LinearLayout ll_namaBaik;
	@BindView(R.id.txt_namaBaik)
	TextView txt_namaBaik;
	private BaseApiService mApiService;
	private Context mContext;
	private AppDatabaseRoom db;
	private String id_kerusakan, id_user, jabatan;
	private boolean isChooseImage;
	private Bitmap bitmap1, bitmap2;
	private RadioButton belum, proses, sudah;
	private LinearLayout penanganan, loading_perbaruan, layout_sukses;
	private ImageView gambar_baik1;
	private ImageView gambar_baik2;
	private String statusPerbaikan = "", tangani;
	private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_list_data);
		ButterKnife.bind(this);

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		id_kerusakan = getIntent().getStringExtra("id_kerusakan");
		id_user = db.userDao().selectData().getId_user();
		jabatan = db.userDao().selectData().getNama_jabatan();

		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			requestData();
		} else {
			TextView txt_close;
			Button btn_coba;
			BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
			@SuppressLint("InflateParams") View sheetView = LayoutInflater.from(mContext).inflate(R.layout.no_connect, null);
			mBottomSheetDialog.setContentView(sheetView);

			txt_close = sheetView.findViewById(R.id.txt_close);
			btn_coba = sheetView.findViewById(R.id.btn_coba);

			mBottomSheetDialog.show();
			Objects.requireNonNull(txt_close).setOnClickListener(v2 -> mBottomSheetDialog.dismiss());

			btn_coba.setOnClickListener(v2 -> {
				requestData();

				ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
				if (netInfo2 != null && netInfo2.isConnected()) {
					mBottomSheetDialog.dismiss();
				}
			});
		}

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Detail Data");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());
		if (jabatan.equals("Penilik")) {
			ll_nama.setVisibility(GONE);
		} else {
			ll_nama.setVisibility(View.VISIBLE);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(jabatan.equals("PPK")){
			getMenuInflater().inflate(R.menu.menu_edit, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_penanganan_true) {
			if(jabatan.equals("PPK")){
				LayoutInflater inflater = getLayoutInflater();
				@SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.layout_edit, null);

				ImageView imv_simpan = alertLayout.findViewById(R.id.imv_simpan);

				RadioGroup radiopenanganan = alertLayout.findViewById(R.id.radiopenanganan);
				belum = alertLayout.findViewById(R.id.belum);
				proses = alertLayout.findViewById(R.id.proses);
				sudah = alertLayout.findViewById(R.id.sudah);

				penanganan = alertLayout.findViewById(R.id.penanganan);
				Button ambil_gambar1 = alertLayout.findViewById(R.id.ambil_gambar1);
				Button ambil_gambar2 = alertLayout.findViewById(R.id.ambil_gambar2);
				gambar_baik1 = alertLayout.findViewById(R.id.gambar_baik1);
				gambar_baik2 = alertLayout.findViewById(R.id.gambar_baik2);

				loading_perbaruan = alertLayout.findViewById(R.id.loading_perbaruan);
				layout_sukses = alertLayout.findViewById(R.id.layout_sukses);

				radiopenanganan.clearCheck();

				switch (statusPerbaikan) {
					case "3":
						imv_simpan.setVisibility(GONE);
						belum.setVisibility(GONE);
						proses.setVisibility(GONE);
						sudah.setVisibility(GONE);
						break;
					case "2":
						belum.setVisibility(GONE);
						proses.setVisibility(GONE);
						break;
					case "1":
						belum.setVisibility(GONE);
						break;
					default:
						belum.setVisibility(View.VISIBLE);
						break;
				}

				ambil_gambar1.setOnClickListener((View view) -> {
					isChooseImage = false;
					showFileChooser();
				});
				ambil_gambar2.setOnClickListener((View view) -> {
					isChooseImage = true;
					showFileChooser();
				});

				radiopenanganan.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
					switch (i) {
						case R.id.belum:
							penanganan.setVisibility(GONE);
							tangani = "1";
							break;
						case R.id.proses:
							penanganan.setVisibility(View.VISIBLE);
							tangani = "2";
							break;
						case R.id.sudah:
							penanganan.setVisibility(View.VISIBLE);
							tangani = "3";
							break;
					}
				});

				if (statusPerbaikan.equalsIgnoreCase("3")) {
					TastyToast.makeText(mContext, "Kerusakan Jalan Sudah Diperbaiki", TastyToast.LENGTH_SHORT, TastyToast.INFO);
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
					alert.setView(alertLayout);
					alert.setCancelable(false);
					alert.setPositiveButton("Exit", (DialogInterface dialog, int which) -> dialog.dismiss());
					AlertDialog dialog = alert.create();
					dialog.show();
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);

					imv_simpan.setOnClickListener((View view) -> {
						if (belum.isChecked() || proses.isChecked() || sudah.isChecked()) {
							String images, images1;
							if (tangani.equals("3")) {
								if (bitmap1 == null || bitmap2 == null) {
									TastyToast.makeText(mContext, "Foto harus Anda isi semua.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
								} else {
									images = getStringImage(bitmap1);
									images1 = getStringImage(bitmap2);
									requestUpdateData(id_kerusakan, images, images1, tangani);
								}
							} else if (tangani.equals("2")) {
								if (bitmap1 == null || bitmap2 == null) {
									TastyToast.makeText(mContext, "Foto harus Anda isi semua.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
								} else {
									images = getStringImage(bitmap1);
									images1 = getStringImage(bitmap2);
									requestUpdateData(id_kerusakan, images, images1, tangani);
								}
							} else {
								TastyToast.makeText(mContext, "Mohon periksa inputan anda.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
							}
						} else {
							TastyToast.makeText(mContext, "Mohon periksa inputan anda.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
						}
					});
				}
			}else{
				TastyToast.makeText(mContext, "Mohon periksa inputan anda.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void showFileChooser() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
			int max_resolution_image = 1000;
			if (isChooseImage) {
				bitmap1 = (Bitmap) data.getExtras().get("data");
				gambar_baik2.setImageBitmap(getResizedBitmap(bitmap1, max_resolution_image));
			} else {
				bitmap2 = (Bitmap) data.getExtras().get("data");
				gambar_baik1.setImageBitmap(getResizedBitmap(bitmap2, max_resolution_image));
			}
		} else {
			return;
		}
	}

	private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();
		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 1) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}

	private String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		return Base64.encodeToString(imageBytes, Base64.DEFAULT);
	}

	private void requestData() {
		mApiService.dataJalanDetailRequest(id_kerusakan, "null", "null")
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						if (response.isSuccessful()) {
							rl_pb.setVisibility(GONE);
							rl_detail.setVisibility(View.VISIBLE);
							RestDataJalan restDataJalan = response.body();
							Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_1()).into(gambar1rusak);
							Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_2()).into(gambar2rusak);
							gambar1rusak.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_1()));
							gambar2rusak.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_1()));

							if ((restDataJalan.getHasil().get(0).getGambar_proses_1() == null && restDataJalan.getHasil().get(0).getGambar_proses_2() == null) || (restDataJalan.getHasil().get(0).getGambar_proses_1().equals("") && restDataJalan.getHasil().get(0).getGambar_proses_2().equals(""))) {
								ll_txtProses.setVisibility(GONE);
								ll_imgProses.setVisibility(GONE);
								ll_tglProses.setVisibility(GONE);
								ll_namaProses.setVisibility(GONE);
							} else {
								txt_namaProses.setText(restDataJalan.getHasil().get(0).getNama_proses());
								Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_proses_1()).into(gambar1proses);
								Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_proses_2()).into(gambar2proses);
								gambar1proses.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_proses_1()));
								gambar2proses.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_proses_2()));
							}

							if ((restDataJalan.getHasil().get(0).getGambar_selesai_1() == null && restDataJalan.getHasil().get(0).getGambar_selesai_2() == null) || (restDataJalan.getHasil().get(0).getGambar_selesai_1().equals("") && restDataJalan.getHasil().get(0).getGambar_selesai_2().equals(""))) {
								ll_txtBaik.setVisibility(GONE);
								ll_imgBaik.setVisibility(GONE);
								ll_tglBaik.setVisibility(GONE);
								ll_namaBaik.setVisibility(GONE);
							} else {
								txt_namaBaik.setText(restDataJalan.getHasil().get(0).getNama_selesai());
								Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_selesai_1()).into(gambar1baik);
								Glide.with(mContext).load(BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_selesai_2()).into(gambar2baik);
								gambar1baik.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_selesai_1()));
								gambar2baik.setOnClickListener(v -> dialogPreview(mContext, BASE_IMG_API + restDataJalan.getHasil().get(0).getGambar_selesai_2()));
							}

							statusPerbaikan = restDataJalan.getHasil().get(0).getId_status();
							txt_nama.setText(restDataJalan.getHasil().get(0).getNama_input());
							txt_status.setText(restDataJalan.getHasil().get(0).getNama_status());
							txt_satker.setText(restDataJalan.getHasil().get(0).getNama_satker());
							txt_ppk.setText(restDataJalan.getHasil().get(0).getNama_ppk());
							txt_tanggalRusak.setText(restDataJalan.getHasil().get(0).getTgl_pengecekan());
							txt_tanggalProses.setText(restDataJalan.getHasil().get(0).getTgl_proses());
							txt_tanggalBaik.setText(restDataJalan.getHasil().get(0).getTgl_selesai());
							txt_kategori.setText(restDataJalan.getHasil().get(0).getNama_kategori());
							txt_jenis.setText(restDataJalan.getHasil().get(0).getNama_kerusakan());
							txt_penangan.setText(restDataJalan.getHasil().get(0).getKet_perbaikan());
							txt_tingkat.setText(restDataJalan.getHasil().get(0).getNama_tingkat());
							txt_lat.setText(restDataJalan.getHasil().get(0).getLat());
							txt_lng.setText(restDataJalan.getHasil().get(0).getLng());
							txt_ket.setText(restDataJalan.getHasil().get(0).getDetail_kerusakan());
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void requestUpdateData(String id_kerusakan, String gambar1_baik, String gambar2_baik, String status) {
		loading_perbaruan.setVisibility(View.VISIBLE);
		mApiService.updatePenilikRequest(id_kerusakan, id_user, gambar1_baik, gambar2_baik, status)
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						if (response.isSuccessful()) {
							loading_perbaruan.setVisibility(View.GONE);
							layout_sukses.setVisibility(View.VISIBLE);
							Intent a = new Intent(mContext, MainActivity.class);
							a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(a);
							Intent b = new Intent(mContext, ListDataActivity.class);
							startActivity(b);
						} else {
							TastyToast.makeText(mContext, "Gagal Memperbarui...", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<Pesan> call, @NonNull Throwable t) {
						TastyToast.makeText(mContext, t.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
					}
				});
	}
}
