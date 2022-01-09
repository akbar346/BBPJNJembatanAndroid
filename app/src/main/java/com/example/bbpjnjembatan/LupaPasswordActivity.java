package com.example.bbpjnjembatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.Pesan;
import com.example.bbpjnjembatan.roomDB.entity.User;
import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LupaPasswordActivity extends AppCompatActivity {
	@BindView(R.id.tfNip)
	TextInputEditText tfNip;
	@BindView(R.id.tfEmail)
	TextInputEditText tfEmail;
	private Context mContext;
	private ProgressDialog loading;
	private Unbinder unbinder;
	private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lupa_password);
		ButterKnife.bind(this);
		unbinder = ButterKnife.bind(this);

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Lupa Password");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());

		mContext = this;
		mApiService = UtilsApi.getAPIService();
    }

	@OnClick(R.id.btnLupa)
	void LupaPassword() {
		loading = ProgressDialog.show(mContext, null, "Mohon Tunggu...", true, false);
		mApiService.lupaPasswordRequest(tfNip.getText().toString(), tfEmail.getText().toString())
				.enqueue(new Callback<Pesan>() {
					@Override
					public void onResponse(@NonNull Call<Pesan> call, @NonNull Response<Pesan> response) {
						loading.dismiss();
						if (response.isSuccessful()) {
							if (response.body().isSuccess()) {
								new AlertDialog.Builder(mContext)
										.setMessage(response.body().getMessage())
										.setPositiveButton("Tutup", (dialog, id) -> dialog.dismiss())
										.setCancelable(false)
										.show();
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
					public void onFailure(@NonNull Call<Pesan> call, @NonNull Throwable t) {
						loading.dismiss();
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}
}
