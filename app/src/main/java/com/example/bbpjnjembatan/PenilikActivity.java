package com.example.bbpjnjembatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.LocationAssistant;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.example.bbpjnjembatan.roomDB.entity.item.JenisKerusakanItem;
import com.example.bbpjnjembatan.roomDB.entity.item.KategoriKerusakanItem;
import com.example.bbpjnjembatan.roomDB.entity.item.TingkatItem;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestJenisKerusakan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestKategoriKerusakan;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestTingkat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenilikActivity extends AppCompatActivity implements LocationAssistant.Listener {
	@BindView(R.id.s_kategori)
	Spinner s_kategori;
	@BindView(R.id.s_jenis)
	Spinner s_jenis;
	@BindView(R.id.s_tingkat)
	Spinner s_tingkat;
	@BindView(R.id.et_keterangan)
	EditText et_keterangan;
	@BindView(R.id.txt_lat)
	TextView txt_lat;
	@BindView(R.id.txt_lng)
	TextView txt_lng;
	@BindView(R.id.btn_simpan)
	Button btn_simpan;
	@BindView(R.id.ambil_gambar)
	Button ambilgambar;
	@BindView(R.id.ambil_gambar1)
	Button ambilgambar1;
	@BindView(R.id.pic)
	ImageView imageView;
	@BindView(R.id.pic1)
	ImageView imageView1;
	int max_resolution_image = 1000;
	LocationManager locationManager;
	String images, images1;
	String id_user, id_satker, id_ppk, idKategori, namaKategori, idJenis, namaJenis, idTingkat, namaTingkat;
	List<TingkatItem> tingkatItems = new ArrayList<>();
	List<KategoriKerusakanItem> kategoriKerusakanItems = new ArrayList<>();
	List<JenisKerusakanItem> jenisKerusakanItems = new ArrayList<>();
	private Context mContext;
	private BaseApiService mApiService;
	private int PICK_IMAGE_REQUEST = 1;
	private boolean isChooseImage = false;
	private LocationAssistant assistant;
	private AppDatabaseRoom db;
	private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
		assistant.setVerbose(true);
		setContentView(R.layout.activity_penilik);
		ButterKnife.bind(this);
		unbinder = ButterKnife.bind(this);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Penilikan");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		UtilsApi.CheckPermisiDiAplikasi(getBaseContext(), this);
		id_user = db.userDao().selectData().getId_user();
		id_satker = db.userDao().selectData().getId_satker();
		id_ppk = db.userDao().selectData().getId_ppk();

		requestTingkat();
		requestKategori();

		ambilgambar.setOnClickListener((View v) -> {
			isChooseImage = false;
			showFileChooser();
		});

		ambilgambar1.setOnClickListener((View v) -> {
			isChooseImage = true;
			showFileChooser();
		});

		btn_simpan.setOnClickListener(v -> {
			ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				sendPost();
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
					sendPost();

					ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
					if (netInfo2 != null && netInfo2.isConnected()) {
						mBottomSheetDialog.dismiss();
					}
				});
			}
		});
    }

	private void sendPost() {
		if (images == null || images1 == null || TextUtils.isEmpty(idKategori) || TextUtils.isEmpty(idJenis) || TextUtils.isEmpty(idTingkat) || TextUtils.isEmpty(et_keterangan.getText().toString())) {
			TastyToast.makeText(mContext, "Inputan harus Anda isi semua", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
		} else {
			requestPenilik();
		}
	}

	private void showFileChooser() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
			if (isChooseImage) {
				Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
				assert bitmap1 != null;
				images1 = getStringImage(bitmap1);
				imageView1.setImageBitmap(getResizedBitmap(bitmap1, max_resolution_image));
			} else {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				assert bitmap != null;
				images = getStringImage(bitmap);
				imageView.setImageBitmap(getResizedBitmap(bitmap, max_resolution_image));
				imageView1.setVisibility(View.VISIBLE);
				ambilgambar1.setVisibility(View.VISIBLE);
			}
		} else {
			return;
		}
	}

	private String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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

	private void requestPenilik() {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.addPenilikRequest(id_user, idKategori, idJenis, idTingkat, images, images1, et_keterangan.getText().toString(), txt_lat.getText().toString(), txt_lng.getText().toString(), id_satker, id_ppk)
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						if (response.isSuccessful()) {
							progressDialog.dismiss();
							if (response.body().isSuccess()) {
								TastyToast.makeText(mContext, response.body().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
								finish();
								Intent i = new Intent(mContext, MainActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
								Intent j = new Intent(mContext, ListDataActivity.class);
								startActivity(j);
							} else {
								TastyToast.makeText(mContext, response.body().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
							}
						} else {
							progressDialog.dismiss();
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<Pesan> call, @NonNull Throwable t) {
						progressDialog.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void requestTingkat() {
		mApiService.tingkatRequest()
				.enqueue(new Callback<RestTingkat>() {
					@Override
					public void onResponse(@NonNull Call<RestTingkat> call, @NonNull Response<RestTingkat> response) {
						if (response.isSuccessful()) {
							RestTingkat restTingkat = response.body();
							if (restTingkat.getSuccess().equals("false")) {
								TastyToast.makeText(mContext, "Kategori kerusakan jalan tidak ada", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							} else {
								tingkatItems = restTingkat.getHasil();
								String[] items = new String[tingkatItems.size()];
								for (int i = 0; i < tingkatItems.size(); i++) {
									items[i] = tingkatItems.get(i).getNama_tingkat();
								}

								HintAdapter<TingkatItem> hintAdapter = new HintAdapter<TingkatItem>(mContext, R.layout.spinner_item, "", tingkatItems) {
									@Override
									protected View getCustomView(int position, View convertView, ViewGroup parent) {
										final TingkatItem user = getItem(position);
										final String nama = user.getNama_tingkat();
										View view = inflateLayout(parent, false);
										((TextView) view.findViewById(R.id.tv_item)).setText(nama);
										return view;
									}
								};

								HintSpinner<TingkatItem> hintSpinner = new HintSpinner<>(
										s_tingkat,
										hintAdapter, (int position, TingkatItem itemAtPosition) -> {
									idTingkat = String.valueOf(tingkatItems.get(position).getId_tingkat());
									namaTingkat = tingkatItems.get(position).getNama_tingkat();
								});
								hintSpinner.init();
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestTingkat> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void requestKategori() {
		mApiService.kategoriKerusakanRequest()
				.enqueue(new Callback<RestKategoriKerusakan>() {
					@Override
					public void onResponse(@NonNull Call<RestKategoriKerusakan> call, @NonNull Response<RestKategoriKerusakan> response) {
						if (response.isSuccessful()) {
							RestKategoriKerusakan restKategoriKerusakan = response.body();
							if (restKategoriKerusakan.getSuccess().equals("false")) {
								TastyToast.makeText(mContext, "Kategori kerusakan jalan tidak ada", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							} else {
								kategoriKerusakanItems = restKategoriKerusakan.getHasil();
								String[] items = new String[kategoriKerusakanItems.size()];
								for (int i = 0; i < kategoriKerusakanItems.size(); i++) {
									items[i] = kategoriKerusakanItems.get(i).getNama_kategori();
								}

								HintAdapter<KategoriKerusakanItem> hintAdapter = new HintAdapter<KategoriKerusakanItem>(mContext, R.layout.spinner_item, "", kategoriKerusakanItems) {
									@Override
									protected View getCustomView(int position, View convertView, ViewGroup parent) {
										final KategoriKerusakanItem user = getItem(position);
										final String nama = user.getNama_kategori();
										View view = inflateLayout(parent, false);
										((TextView) view.findViewById(R.id.tv_item)).setText(nama);
										return view;
									}
								};

								HintSpinner<KategoriKerusakanItem> hintSpinner = new HintSpinner<>(
										s_kategori,
										hintAdapter, (int position, KategoriKerusakanItem itemAtPosition) -> {
									idKategori = String.valueOf(kategoriKerusakanItems.get(position).getId_kategori());
									namaKategori = kategoriKerusakanItems.get(position).getNama_kategori();
									requestJenis();
								});
								hintSpinner.init();
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestKategoriKerusakan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void requestJenis() {
		mApiService.jenisKerusakanRequest(idKategori)
				.enqueue(new Callback<RestJenisKerusakan>() {
					@Override
					public void onResponse(@NonNull Call<RestJenisKerusakan> call, @NonNull Response<RestJenisKerusakan> response) {
						if (response.isSuccessful()) {
							RestJenisKerusakan restJenisKerusakan = response.body();
							if (restJenisKerusakan.getSuccess().equals("false")) {
								TastyToast.makeText(mContext, "Jenis kerusakan jalan tidak ada", TastyToast.LENGTH_SHORT, TastyToast.INFO);
							} else {
								jenisKerusakanItems = restJenisKerusakan.getHasil();
								String[] items = new String[jenisKerusakanItems.size()];
								for (int i = 0; i < jenisKerusakanItems.size(); i++) {
									items[i] = jenisKerusakanItems.get(i).getNama_kerusakan();
								}

								HintAdapter<JenisKerusakanItem> hintAdapter = new HintAdapter<JenisKerusakanItem>(mContext, R.layout.spinner_item, "", jenisKerusakanItems) {
									@Override
									protected View getCustomView(int position, View convertView, ViewGroup parent) {
										final JenisKerusakanItem user = getItem(position);
										final String nama = user.getNama_kerusakan();
										View view = inflateLayout(parent, false);
										((TextView) view.findViewById(R.id.tv_item)).setText(nama);
										return view;
									}
								};

								HintSpinner<JenisKerusakanItem> hintSpinner = new HintSpinner<>(
										s_jenis,
										hintAdapter, (int position, JenisKerusakanItem itemAtPosition) -> {
									idJenis = String.valueOf(jenisKerusakanItems.get(position).getId_perbaikan());
									namaJenis = jenisKerusakanItems.get(position).getNama_kerusakan();
								});
								hintSpinner.init();
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestJenisKerusakan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
			locationManager.removeUpdates((LocationListener) this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		assistant.start();
	}

	@Override
	protected void onPause() {
		assistant.stop();
		super.onPause();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (assistant.onPermissionsUpdated(requestCode, grantResults)) {
			txt_lat.setOnClickListener(null);
			txt_lng.setOnClickListener(null);
		}
	}

	@Override
	public void onNeedLocationPermission() {
		txt_lat.setText("Need Permission");
		txt_lng.setText("Need Permission");
		txt_lat.setOnClickListener(view -> assistant.requestLocationPermission());
		txt_lng.setOnClickListener(view -> assistant.requestLocationPermission());
		assistant.requestAndPossiblyExplainLocationPermission();
	}

	@Override
	public void onExplainLocationPermission() {
		new android.app.AlertDialog.Builder(this)
				.setMessage(R.string.permissionExplanation)
				.setPositiveButton(R.string.ok, (dialog, which) -> {
					dialog.dismiss();
					assistant.requestLocationPermission();
				})
				.setNegativeButton(R.string.cancel, (dialog, which) -> {
					dialog.dismiss();
					txt_lat.setOnClickListener(v -> assistant.requestLocationPermission());
					txt_lng.setOnClickListener(v -> assistant.requestLocationPermission());
				})
				.setCancelable(false)
				.show();
	}

	@Override
	public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
		new android.app.AlertDialog.Builder(this)
				.setMessage(R.string.permissionPermanentlyDeclined)
				.setPositiveButton(R.string.ok, fromDialog)
				.setCancelable(false)
				.show();
	}

	@Override
	public void onNeedLocationSettingsChange() {
		new android.app.AlertDialog.Builder(this)
				.setMessage(R.string.switchOnLocationShort)
				.setPositiveButton(R.string.ok, (dialog, which) -> {
					dialog.dismiss();
					assistant.changeLocationSettings();
				})
				.setCancelable(false)
				.show();
	}

	@Override
	public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
		new AlertDialog.Builder(this)
				.setMessage(R.string.switchOnLocationLong)
				.setPositiveButton(R.string.ok, fromDialog)
				.setCancelable(false)
				.show();
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onNewLocationAvailable(Location location) {
		if (location == null) return;
		txt_lat.setText(String.valueOf(location.getLatitude()));
		txt_lng.setText(String.valueOf(location.getLongitude()));
	}

	@Override
	public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
		txt_lat.setText(getString(R.string.mockLocationMessage));
		txt_lng.setText(getString(R.string.mockLocationMessage));
		txt_lat.setOnClickListener(fromView);
		txt_lng.setOnClickListener(fromView);
	}

	@Override
	public void onError(LocationAssistant.ErrorType type, String message) {
		txt_lat.setText(getString(R.string.error));
		txt_lng.setText(getString(R.string.error));
	}
}
