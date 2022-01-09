package com.example.bbpjnjembatan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.example.bbpjnjembatan.roomDB.entity.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements LocationListener {
	@BindView(R.id.tfNip)
	TextInputEditText tfNip;
	@BindView(R.id.tfPassword)
	TextInputEditText tfPassword;
	private Context mContext;
	private AppDatabaseRoom db;
	private ProgressDialog loading;
	private Unbinder unbinder;
	private BaseApiService mApiService;

	//lokasi
	private Location loc;
	boolean isNetwork = false;
	private LocationManager locationManager;
	private boolean canGetLocation = true;
	private boolean isGPS = false;
	private ArrayList<String> permissions = new ArrayList<>();
	private ArrayList<String> permissionsToRequest;
	private ArrayList<String> permissionsRejected = new ArrayList<>();
	private final static int ALL_PERMISSIONS_RESULT = 101;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
	private static final long MIN_TIME_BW_UPDATES = 1000;
	private final String TAG = "GPS";
	private String lat = "", lng = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		unbinder = ButterKnife.bind(this);

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		// location
		locationManager = (LocationManager) mContext.getSystemService(Service.LOCATION_SERVICE);
		isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
		permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		permissionsToRequest = findUnAskedPermissions(permissions);

		if (!isGPS && !isNetwork) {
			Log.d(TAG, "Connection off");
			showSettingsAlert();
			getLastLocation();
		} else {
			Log.d(TAG, "Connection on");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (permissionsToRequest.size() > 0) {
					requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
							ALL_PERMISSIONS_RESULT);
					Log.d(TAG, "Permission requests");
					canGetLocation = false;
				}
			}
			getLocation();
		}
	}

	@OnClick(R.id.btnLupa)
	void LupaPassword() {
		Intent i = new Intent(mContext, LupaPasswordActivity.class);
		startActivity(i);
	}

	@OnClick(R.id.btnMasuk)
	void Login() {
		String username = tfNip.getText().toString();
		String password = tfPassword.getText().toString();

		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			if (TextUtils.isEmpty(username)) {
				tfNip.setError("NIP tidak boleh kosong!");
			} else {
				tfNip.setError(null);
			}

			if (TextUtils.isEmpty(password)) {
				tfPassword.setError("Password tidak boleh kosong!");
			} else {
				tfPassword.setError(null);
			}
		} else {
			ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				requestLogin();
			} else {
				TextView txt_close;
				Button btn_coba;
				BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
				@SuppressLint("InflateParams") View sheetView = LayoutInflater.from(mContext).inflate(R.layout.no_connect, null);
				mBottomSheetDialog.setContentView(sheetView);

				txt_close = sheetView.findViewById(R.id.txt_close);
				btn_coba = sheetView.findViewById(R.id.btn_coba);

				mBottomSheetDialog.show();
				mBottomSheetDialog.setCancelable(false);
				Objects.requireNonNull(txt_close).setOnClickListener(v -> mBottomSheetDialog.dismiss());

				btn_coba.setOnClickListener(v -> {
					requestLogin();
					ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
					if (netInfo2 != null && netInfo2.isConnected()) {
						mBottomSheetDialog.dismiss();
					}
				});
			}
		}
	}

	private void requestLogin() {
		loading = ProgressDialog.show(mContext, null, "Mohon Tunggu...", true, false);
		mApiService.loginRequest(tfNip.getText().toString(), tfPassword.getText().toString())
				.enqueue(new Callback<User>() {
					@Override
					public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
						loading.dismiss();
						if (response.isSuccessful()) {
							if (response.body().isSuccess()) {
								User model = new User();
								model.setId_user(response.body().getId_user());
								model.setNama_lengkap(response.body().getNama_lengkap());
								model.setEmail(response.body().getEmail());
								model.setNo_hp(response.body().getNo_hp());
								model.setNip(response.body().getNip());
								model.setId_jabatan(response.body().getId_jabatan());
								model.setNama_jabatan(response.body().getNama_jabatan());
								model.setId_ppk(response.body().getId_ppk());
								model.setKode_ppk(response.body().getKode_ppk());
								model.setNama_ppk(response.body().getNama_ppk());
								model.setId_satker(response.body().getId_satker());
								model.setKode_satker(response.body().getKode_satker());
								model.setNama_satker(response.body().getNama_satker());
								db.userDao().insert(model);

								Intent i = new Intent(mContext, MainActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
								finish();
							} else {
								new AlertDialog.Builder(mContext)
										.setMessage(response.body().getMessage())
										.setPositiveButton("Tutup", (dialog, id) -> dialog.dismiss())
										.setCancelable(false)
										.show();
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
						loading.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	//for gps
	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		updateUI(location);
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
	}

	@Override
	public void onProviderEnabled(String s) {
		getLocation();
	}

	@Override
	public void onProviderDisabled(String s) {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	private void getLocation() {
		try {
			if (canGetLocation) {
				Log.d(TAG, "Can get location");
				if (isGPS) {
					// from GPS
					Log.d(TAG, "GPS on");
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (loc != null)
							updateUI(loc);
					}
				} else if (isNetwork) {
					// from Network Provider
					Log.d(TAG, "NETWORK_PROVIDER on");
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (loc != null)
							updateUI(loc);
					}
				} else {
					loc.setLatitude(0);
					loc.setLongitude(0);
					updateUI(loc);
				}
			} else {
				Log.d(TAG, "Can't get location");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private void getLastLocation() {
		try {
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);
			Log.d(TAG, provider);
			Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
		ArrayList result = new ArrayList();
		for (String perm : wanted) {
			if (!hasPermission(perm)) {
				result.add(perm);
			}
		}
		return result;
	}

	private boolean hasPermission(String permission) {
		if (canAskPermission()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				return (mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
			}
		}
		return true;
	}

	private boolean canAskPermission() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == ALL_PERMISSIONS_RESULT) {
			Log.d(TAG, "onRequestPermissionsResult");
			for (String perms : permissionsToRequest) {
				if (!hasPermission(perms)) {
					permissionsRejected.add(perms);
				}
			}

			if (permissionsRejected.size() > 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
						showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
								(dialog, which) -> {
									requestPermissions(permissionsRejected.toArray(
											new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
								});
					}
				}
			} else {
				Log.d(TAG, "No rejected permissions.");
				canGetLocation = true;
				getLocation();
			}
		}
	}

	public void showSettingsAlert() {
		androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS is not Enabled!");
		alertDialog.setMessage("Do you want to turn on GPS?");
		alertDialog.setPositiveButton("Yes", (dialog, which) -> {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		});
		alertDialog.setNegativeButton("No", (dialog, which) -> finish());
		alertDialog.show();
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new androidx.appcompat.app.AlertDialog.Builder(mContext)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	@SuppressLint("SetTextI18n")
	private void updateUI(Location loc) {
		Log.e(TAG, "updateUI");
		lat = String.valueOf(loc.getLatitude());
		lng = String.valueOf(loc.getLongitude());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}
}
