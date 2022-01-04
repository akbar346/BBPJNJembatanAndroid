package com.example.bbpjnjembatan;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.tastytoast.TastyToast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahPassword extends Fragment {
	@BindView(R.id.tfPwLama)
	TextInputEditText tfPwLama;
	@BindView(R.id.tfPwBaru)
	TextInputEditText tfPwBaru;
	@BindView(R.id.btn_simpan)
	Button btn_simpan;
	private ProgressDialog loading;
	private Context mContext;
	private BaseApiService mApiService;
	private AppDatabaseRoom db;
	private String id_user;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ubah_password, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mContext = getActivity();
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(mContext.getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		id_user = db.userDao().selectData().getId_user();

		btn_simpan.setOnClickListener(v -> {
			String pwLama = tfPwLama.getText().toString();
			String pwBaru = tfPwBaru.getText().toString();

			if (pwLama.trim().length() > 0 && pwBaru.trim().length() > 0) {
				ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnected()) {
					requestubahPassword();
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
						requestubahPassword();

						ConnectivityManager cm2 = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
						if (netInfo2 != null && netInfo2.isConnected()) {
							mBottomSheetDialog.dismiss();
						}
					});
				}
			} else {
				TastyToast.makeText(mContext, "Inputan harus Anda isi semua", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
			}
		});
	}

	private void requestubahPassword() {
		loading = ProgressDialog.show(mContext, null, "Mohon Tunggu...", true, false);
		mApiService.ubahPasswordRequest(id_user, tfPwLama.getText().toString(), tfPwBaru.getText().toString())
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						loading.dismiss();
						if (response.isSuccessful()) {
							if (response.body().isSuccess()) {
								TastyToast.makeText(mContext, response.body().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
							} else {
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
}
