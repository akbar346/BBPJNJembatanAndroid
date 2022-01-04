package com.example.bbpjnjembatan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.LocationAssistant;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements LocationAssistant.Listener {
	@BindView(R.id.cv_penilik)
	CardView cv_penilik;
	@BindView(R.id.tv_koordinat)
	TextView tv_koordinat;
	private Context mContext;
	private AppDatabaseRoom db;
	private Unbinder unbinder;
	private BaseApiService mApiService;
	private boolean doubleBackToExitPressedOnce = false;
	private String lat = "", lng = "";
	private LocationAssistant assistant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
		assistant.setVerbose(true);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		unbinder = ButterKnife.bind(this);

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		if (db.userDao().selectData().getNama_jabatan().equals("Penilik")) {
			cv_penilik.setVisibility(View.VISIBLE);
		} else {
			cv_penilik.setVisibility(View.GONE);
		}
	}

	@OnClick(R.id.cv_maps)
	void Maps() {
		Intent i = new Intent(mContext, MapsActivity.class);
		startActivity(i);
	}

	@OnClick(R.id.cv_penilik)
	void Penilik() {
		Intent i = new Intent(mContext, PenilikActivity.class);
		startActivity(i);
	}

	@OnClick(R.id.cv_data)
	void Data() {
		Intent i = new Intent(mContext, ListDataActivity.class);
		startActivity(i);
	}

	@OnClick(R.id.cv_info)
	void Info() {
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_tentang_aplikasi);
		dialog.show();

		TextView txt_versi;
		txt_versi = dialog.findViewById(R.id.txt_versi);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			txt_versi.setText("Versi " + pInfo.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.cv_pengaturan)
	void Pengaturan() {
		Intent i = new Intent(mContext, PengaturanActivity.class);
		startActivity(i);
	}

	@OnClick(R.id.iv_logout)
	void Keluar() {
		new androidx.appcompat.app.AlertDialog.Builder(mContext)
				.setMessage("Anda yakin ingin keluar dari aplikasi?")
				.setCancelable(false)
				.setPositiveButton("Iya", (dialog, which) -> {
					db.userDao().deleteAll();
					Intent i = new Intent(mContext, LoginActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					finish();
				})
				.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel()).show();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(mContext, "Ketuk lagi untuk keluar", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
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
		if (assistant.onPermissionsUpdated(requestCode, grantResults))
			tv_koordinat.setOnClickListener(null);
	}

	@Override
	public void onNeedLocationPermission() {
		tv_koordinat.setText("Need Permission");
		tv_koordinat.setOnClickListener(view -> assistant.requestLocationPermission());
		assistant.requestAndPossiblyExplainLocationPermission();
	}

	@Override
	public void onExplainLocationPermission() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.permissionExplanation)
				.setPositiveButton(R.string.ok, (dialog, which) -> {
					dialog.dismiss();
					assistant.requestLocationPermission();
				})
				.setNegativeButton(R.string.cancel, (dialog, which) -> {
					dialog.dismiss();
					tv_koordinat.setOnClickListener(v -> assistant.requestLocationPermission());
				})
				.setCancelable(false)
				.show();
	}

	@Override
	public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
		new AlertDialog.Builder(this)
				.setMessage(R.string.permissionPermanentlyDeclined)
				.setPositiveButton(R.string.ok, fromDialog)
				.setCancelable(false)
				.show();
	}

	@Override
	public void onNeedLocationSettingsChange() {
		new AlertDialog.Builder(this)
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
		lat = String.valueOf(location.getLatitude());
		lng = String.valueOf(location.getLongitude());
		tv_koordinat.setText("Koordinat: " + lat + ", " + lng);
	}

	@Override
	public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
		tv_koordinat.setText(getString(R.string.mockLocationMessage));
		tv_koordinat.setOnClickListener(fromView);
	}

	@Override
	public void onError(LocationAssistant.ErrorType type, String message) {
		tv_koordinat.setText(getString(R.string.error));
	}
}
