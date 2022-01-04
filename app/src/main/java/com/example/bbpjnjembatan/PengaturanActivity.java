package com.example.bbpjnjembatan;

import static com.example.bbpjnjembatan.config.UtilsApi.BASE_IMG_MARKER;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengaturanActivity extends AppCompatActivity {
	@BindView(R.id.img_profil)
	CircleImageView img_profil;
	@BindView(R.id.txt_namaProfil)
	TextView txt_namaProfil;
	@BindView(R.id.txt_jabatan)
	TextView txt_jabatan;
	@BindView(R.id.txt_satker)
	TextView txt_satker;
	@BindView(R.id.txt_ppk)
	TextView txt_ppk;
	@BindView(R.id.btn_uploadProfil)
	ImageButton btn_uploadProfil;
	@BindView(R.id.viewpager)
	ViewPager viewpager;
	@BindView(R.id.tabs)
	TabLayout tabs;
	private ProgressDialog loading;
	private Context mContext;
	private BaseApiService mApiService;
	private Bitmap bitmap;
	private String id_user;
	private AppDatabaseRoom db;
	private int DOK = 1;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pengaturan);
		ButterKnife.bind(this);

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Pengaturan");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		id_user = db.userDao().selectData().getId_user();
		String role = db.userDao().selectData().getNama_jabatan();

		if (role.equalsIgnoreCase("Satker")) {
			txt_ppk.setVisibility(View.GONE);
		} else {
			txt_ppk.setVisibility(View.VISIBLE);
		}

		txt_namaProfil.setText(db.userDao().selectData().getNama_lengkap());
		txt_jabatan.setText("Jabatan " + db.userDao().selectData().getNama_jabatan());
		txt_satker.setText("Satker " + db.userDao().selectData().getNama_satker());
		txt_ppk.setText("PPK " + db.userDao().selectData().getNama_ppk());

		setupViewPager(viewpager);
		tabs.setupWithViewPager(viewpager);
		tabs.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
		tabs.setTabTextColors(Color.parseColor("#E0E0E0"), Color.parseColor("#FAFAFA"));

		btn_uploadProfil.setOnClickListener(v -> choosePhotoNpwpFromGallery());

		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			requestdataPegawai();
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
				requestdataPegawai();

				ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
				if (netInfo2 != null && netInfo2.isConnected()) {
					mBottomSheetDialog.dismiss();
				}
			});
		}
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFrag(new DataDiri(), "Data Diri");
		adapter.addFrag(new UbahPassword(), "Ubah Password");
		viewPager.setAdapter(adapter);
	}

	private String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		return Base64.encodeToString(imageBytes, Base64.DEFAULT);
	}

	// fungsi resize image
	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

	public void choosePhotoNpwpFromGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, DOK);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		try {
			Uri contentURI = data.getData();
			if (requestCode == DOK) {
				Bitmap bitmapKk = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
				int max_resolution_image = 1000;
				bitmap = getResizedBitmap(bitmapKk, max_resolution_image);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				img_profil.setImageBitmap(bitmap);

				ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnected()) {
					requestubahFoto();
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
						requestubahFoto();

						ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
						if (netInfo2 != null && netInfo2.isConnected()) {
							mBottomSheetDialog.dismiss();
						}
					});
				}
			} else {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
		}
	}

	private void requestdataPegawai() {
		mApiService.dataRequest(id_user)
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						if (response.isSuccessful()) {
							if (response.body().isSuccess()) {
								Glide.with(mContext).load(BASE_IMG_MARKER + response.body().getFoto()).into(img_profil);
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<Pesan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void requestubahFoto() {
		loading = ProgressDialog.show(mContext, null, "Mohon Tunggu...", true, false);
		String s_foto = getStringImage(bitmap);
		mApiService.ubahFotoRequest(id_user, s_foto)
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						loading.dismiss();
						if (response.isSuccessful()) {
							if (response.body().isSuccess()) {
								TastyToast.makeText(mContext, response.body().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
							}else{
								TastyToast.makeText(mContext, response.body().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
							}
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<Pesan> call, @NonNull Throwable t) {
						loading.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@NonNull
		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		void addFrag(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
}
