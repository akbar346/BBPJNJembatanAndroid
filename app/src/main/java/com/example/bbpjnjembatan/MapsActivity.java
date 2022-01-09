package com.example.bbpjnjembatan;

import static com.example.bbpjnjembatan.config.UtilsApi.BASE_IMG_MARKER;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.item.DataJalanItems;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestDataJalan;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
	LatLng center = null, latLng = null;
	SupportMapFragment mapFragment = null;
	GoogleMap gMap = null;
	MarkerOptions markerOptions;
	CameraPosition cameraPosition = null;
	// Tracking
	String goolgeMap = "com.google.android.apps.maps";
	Uri gmmIntentUri;
	Intent mapIntent;
	List<DataJalanItems> dataJalanItems = new ArrayList<>();
	private Context mContext;
	private BaseApiService mApiService;
	private String id_user, id_ppk, id_satker;
	private AppDatabaseRoom db;
	private Unbinder unbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		ButterKnife.bind(this);
		unbinder = ButterKnife.bind(this);

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Maps");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		UtilsApi.CheckPermisiDiAplikasi(getBaseContext(), this);

		id_user = db.userDao().selectData().getId_user();
		id_satker = db.userDao().selectData().getId_satker();
		id_ppk = db.userDao().selectData().getId_ppk();

		markerOptions = new MarkerOptions();

		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		gMap = googleMap;
		center = new LatLng(-7.2747627, 112.7607764);
		cameraPosition = new CameraPosition.Builder().target(center).zoom(12f).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		gMap.getUiSettings().isZoomControlsEnabled();
		gMap.getUiSettings().isMyLocationButtonEnabled();

		String jabatan = db.userDao().selectData().getNama_jabatan();

		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			assert jabatan != null;
			if (jabatan.equalsIgnoreCase("Satker")) {
				showMapsSatker();
			} else if (jabatan.equalsIgnoreCase("PPK")) {
				showMapsPpk();
			} else if (jabatan.equalsIgnoreCase("Penilik")) {
				showMapsPegawai();
			}
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
				if (jabatan.equalsIgnoreCase("Satker")) {
					showMapsSatker();
				} else if (jabatan.equalsIgnoreCase("PPK")) {
					showMapsPpk();
				} else if (jabatan.equalsIgnoreCase("Penilik")) {
					showMapsPegawai();
				}

				ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
				if (netInfo2 != null && netInfo2.isConnected()) {
					mBottomSheetDialog.dismiss();
				}
			});
		}
	}

	private void addMarker(LatLng latlng, String id, String kordinat, String img) {
		markerOptions.position(latlng);
		markerOptions.title(id);
		markerOptions.snippet(kordinat);
		markerOptions.flat(true);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(img)));
		gMap.addMarker(markerOptions);
		gMap.setOnMarkerClickListener(marker -> {
			final Dialog dialog = new Dialog(mContext);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.layout_custom_dialog);
			LinearLayout ll_detail;
			TextView txt_nama, txt_status, txt_satker, txt_ppk, txt_tanggalRusak, txt_kategori, txt_jenis, txt_tingkat, txt_ket;
			Button btn_rute, btn_detail;

			ll_detail = dialog.findViewById(R.id.ll_detail);
			txt_nama = dialog.findViewById(R.id.txt_nama);
			txt_status = dialog.findViewById(R.id.txt_status);
			txt_satker = dialog.findViewById(R.id.txt_satker);
			txt_ppk = dialog.findViewById(R.id.txt_ppk);
			txt_tanggalRusak = dialog.findViewById(R.id.txt_tanggalRusak);
			txt_kategori = dialog.findViewById(R.id.txt_kategori);
			txt_jenis = dialog.findViewById(R.id.txt_jenis);
			txt_tingkat = dialog.findViewById(R.id.txt_tingkat);
			txt_ket = dialog.findViewById(R.id.txt_ket);
			btn_rute = dialog.findViewById(R.id.btn_rute);
			btn_detail = dialog.findViewById(R.id.btn_detail);

			mApiService.dataJalanDetailRequest(marker.getTitle(), "1", "")
					.enqueue(new Callback<RestDataJalan>() {
						@SuppressLint("QueryPermissionsNeeded")
						@Override
						public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
							if (response.isSuccessful()) {
								RestDataJalan restDataJalan = response.body();
								assert restDataJalan != null;
								txt_nama.setText(restDataJalan.getHasil().get(0).getNama_input());
								txt_status.setText(restDataJalan.getHasil().get(0).getNama_status());
								txt_satker.setText(restDataJalan.getHasil().get(0).getNama_satker());
								txt_ppk.setText(restDataJalan.getHasil().get(0).getNama_ppk());
								txt_tanggalRusak.setText(restDataJalan.getHasil().get(0).getTgl_pengecekan());
								txt_kategori.setText(restDataJalan.getHasil().get(0).getNama_kategori());
								txt_jenis.setText(restDataJalan.getHasil().get(0).getNama_kerusakan());
								txt_tingkat.setText(restDataJalan.getHasil().get(0).getNama_tingkat());
								txt_ket.setText(restDataJalan.getHasil().get(0).getDetail_kerusakan());

								btn_rute.setOnClickListener(v -> {
									gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getSnippet());
									mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
									mapIntent.setPackage(goolgeMap);

									startActivity(mapIntent);
//									if (mapIntent.resolveActivity(getPackageManager()) != null) {
//										startActivity(mapIntent);
//									} else {
//										TastyToast.makeText(mContext, "Google Maps Belum Terinstal", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
//									}
								});

								btn_detail.setOnClickListener(v -> {
									Intent i = new Intent(mContext, DetailListDataActivity.class);
									i.putExtra("id_kerusakan", restDataJalan.getHasil().get(0).getId_kerusakan());
									startActivity(i);
								});

								ll_detail.setVisibility(View.VISIBLE);
							} else {
								TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
							}
						}

						@Override
						public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
							Log.e("debug", "onFailure: ERROR > " + t.toString());
						}
					});
			dialog.show();
			return true;
		});
	}

	private void showMapsSatker() {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanSatkerRequest(id_satker, "", "")
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							RestDataJalan restDataJalan = response.body();
							dataJalanItems = restDataJalan.getHasil();
							if (restDataJalan.getSuccess().trim().equals("true")) {
								for (int i = 0; i < dataJalanItems.size(); i++) {
									String id = dataJalanItems.get(i).getId_kerusakan();
									String koordinat = dataJalanItems.get(i).getLat() + "," + dataJalanItems.get(i).getLng();
									String img = BASE_IMG_MARKER + dataJalanItems.get(i).getMarker();
									latLng = new LatLng(Double.parseDouble(dataJalanItems.get(i).getLat()), Double.parseDouble(dataJalanItems.get(i).getLng()));
									addMarker(latLng, id, koordinat, img);
								}
							} else {
								TastyToast.makeText(mContext, "Data tidak ada.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						progressDialog.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void showMapsPpk() {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanPpkRequest(id_ppk, "", "")
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							RestDataJalan restDataJalan = response.body();
							dataJalanItems = restDataJalan.getHasil();
							if (restDataJalan.getSuccess().equals("true")) {
								for (int i = 0; i < dataJalanItems.size(); i++) {
									String id = dataJalanItems.get(i).getId_kerusakan();
									String koordinat = dataJalanItems.get(i).getLat() + "," + dataJalanItems.get(i).getLng();
									String img = BASE_IMG_MARKER + dataJalanItems.get(i).getMarker();
									latLng = new LatLng(Double.parseDouble(dataJalanItems.get(i).getLat()), Double.parseDouble(dataJalanItems.get(i).getLng()));
									addMarker(latLng, id, koordinat, img);
								}
							} else {
								TastyToast.makeText(mContext, "Data tidak ada.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						progressDialog.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void showMapsPegawai() {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanPegawaiRequest(id_user, "", "")
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							RestDataJalan restDataJalan = response.body();
							dataJalanItems = restDataJalan.getHasil();
							if (restDataJalan.getSuccess().equals("true")) {
								for (int i = 0; i < dataJalanItems.size(); i++) {
									String id = dataJalanItems.get(i).getId_kerusakan();
									String koordinat = dataJalanItems.get(i).getLat() + "," + dataJalanItems.get(i).getLng();
									String img = BASE_IMG_MARKER + dataJalanItems.get(i).getMarker();
									latLng = new LatLng(Double.parseDouble(dataJalanItems.get(i).getLat()), Double.parseDouble(dataJalanItems.get(i).getLng()));
									addMarker(latLng, id, koordinat, img);
								}
							} else {
								TastyToast.makeText(mContext, "Data tidak ada.", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						progressDialog.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private Bitmap getBitmapFromURL(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			return BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
