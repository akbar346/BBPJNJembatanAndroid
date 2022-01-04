package com.example.bbpjnjembatan;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
	@BindView(R.id.splash_screen_progress_bar)
	ProgressBar mProgress;
	@BindView(R.id.tv_versi)
	TextView tv_versi;
	private String id_user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ButterKnife.bind(this);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		AppDatabaseRoom db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();

		if (db.userDao().selectData() != null) {
			id_user = db.userDao().selectData().getId_user();
		} else {
			id_user = null;
		}

		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.edit()
				.putBoolean("isFirstRun", true)
				.apply();

		int versionNumber = pinfo.versionCode;

		tv_versi.setText("Ver. " + pinfo.versionName);

		new Thread(this::doWork).start();
	}

	private void doWork() {
		for (int progress = 0; progress <= 100; progress += 20) {
			try {
				Thread.sleep(700);
				mProgress.setProgress(progress);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.runOnUiThread(this::startApp);
	}

	private void startApp() {
		if (id_user == null) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		} else {
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		}
	}
}
