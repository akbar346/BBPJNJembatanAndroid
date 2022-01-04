package com.example.bbpjnjembatan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataDiri extends Fragment {
	@BindView(R.id.txt_username)
	TextView txt_username;
	@BindView(R.id.txt_nama)
	TextView txt_nama;
	@BindView(R.id.txt_email)
	TextView txt_email;
	@BindView(R.id.txt_hp)
	TextView txt_hp;
	@BindView(R.id.txt_dataJabatan)
	TextView txt_dataJabatan;
	@BindView(R.id.txt_dataSatker)
	TextView txt_dataSatker;
	@BindView(R.id.txt_dataPpk)
	TextView txt_dataPpk;
	private AppDatabaseRoom db;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_data_diri, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Context mContext = getActivity();
		db = Room.databaseBuilder(mContext.getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		txt_username.setText(db.userDao().selectData().getNip());
		txt_nama.setText(db.userDao().selectData().getNama_lengkap());
		txt_email.setText(db.userDao().selectData().getId_user());
		txt_hp.setText(db.userDao().selectData().getNo_hp());
		txt_dataJabatan.setText(db.userDao().selectData().getNama_jabatan());
		txt_dataSatker.setText(db.userDao().selectData().getNama_satker());
		txt_dataPpk.setText(db.userDao().selectData().getNama_ppk());
	}
}
