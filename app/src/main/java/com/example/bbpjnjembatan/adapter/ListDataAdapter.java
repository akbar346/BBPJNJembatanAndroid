package com.example.bbpjnjembatan.adapter;

import static com.example.bbpjnjembatan.config.UtilsApi.BASE_IMG_API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.bbpjnjembatan.DetailListDataActivity;
import com.example.bbpjnjembatan.R;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.item.DataJalanItems;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int TYPE_DATA = 0;
	private final int TYPE_LOAD = 1;
	private Context context;
	private List<DataJalanItems> datalist;
	private OnLoadMoreListener loadMoreListener;
	private boolean isLoading = false, isMoreDataAvailable = true;

	public ListDataAdapter(Context context, List<DataJalanItems> datalist) {
		this.context = context;
		this.datalist = datalist;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (viewType == TYPE_DATA) {
			return new DataHolder(inflater.inflate(R.layout.single_data_penilik, parent, false));
		} else {
			return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
			isLoading = true;
			loadMoreListener.onLoadMore();
		}

		if (getItemViewType(position) == TYPE_DATA) {
			((DataHolder) holder).bindData(datalist.get(position), position);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (datalist.get(position).getType().equals("datas")) {
			return TYPE_DATA;
		} else {
			return TYPE_LOAD;
		}
	}

	@Override
	public int getItemCount() {
		return (datalist == null) ? 0 : datalist.size();
	}

	public List<DataJalanItems> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<DataJalanItems> datalist) {
		this.datalist = datalist;
	}

	public void setMoreDataAvailable(boolean moreDataAvailable) {
		isMoreDataAvailable = moreDataAvailable;
	}

	public void notifyDataChanged() {
		notifyDataSetChanged();
		isLoading = false;
	}

	public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	public interface OnLoadMoreListener {
		void onLoadMore();
	}

	private static class LoadHolder extends RecyclerView.ViewHolder {
		private LoadHolder(View itemView) {
			super(itemView);
		}
	}

	class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.txt_id_kerusakan)
		TextView txt_id_kerusakan;
		@BindView(R.id.txt_namaPenilik)
		TextView txt_namaPenilik;
		@BindView(R.id.txt_namaSatker)
		TextView txt_namaSatker;
		@BindView(R.id.txt_namaPpk)
		TextView txt_namaPpk;
		@BindView(R.id.txt_kategoriKerusakan)
		TextView txt_kategoriKerusakan;
		@BindView(R.id.txt_tanggalRusak)
		TextView txt_tanggalRusak;
		@BindView(R.id.txt_tingkatKerusakan)
		TextView txt_tingkatKerusakan;
		@BindView(R.id.txt_status)
		TextView txt_status;
		@BindView(R.id.img1)
		ImageView img1;
		@BindView(R.id.img2)
		ImageView img2;
		@BindView(R.id.ll_nama)
		LinearLayout ll_nama;
		@BindView(R.id.checkBox1)
		CheckBox checkBox;

		private DataHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			itemView.setOnClickListener(this);
		}

		@SuppressLint("SetTextI18n")
		void bindData(DataJalanItems dataModel, int position) {
			AppDatabaseRoom db = Room.databaseBuilder(context.getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();

			switch (db.userDao().selectData().getNama_jabatan()) {
				case "Penilik":
					ll_nama.setVisibility(View.GONE);
					break;
				case "PPK":
				case "Satker":
					ll_nama.setVisibility(View.VISIBLE);
					break;
				default:
					break;
			}

			DataJalanItems listJalan = datalist.get(position);
			checkBox.setText("Checkbox " + position);
			checkBox.setChecked(listJalan.isSelected());
			checkBox.setTag(position);
			checkBox.setOnClickListener((View v) -> {
				listJalan.setSelected(checkBox.isChecked());
			});

			txt_id_kerusakan.setText(dataModel.getId_kerusakan());
			txt_namaPpk.setText(dataModel.getNama_ppk());
			txt_namaPenilik.setText(dataModel.getNama_input());
			Glide.with(context).load(BASE_IMG_API + dataModel.getGambar_1()).into(img1);
			Glide.with(context).load(BASE_IMG_API + dataModel.getGambar_2()).into(img2);
			txt_kategoriKerusakan.setText(dataModel.getNama_kategori());
			txt_tingkatKerusakan.setText(dataModel.getNama_tingkat());
			txt_tanggalRusak.setText(dataModel.getTgl_pengecekan());

			switch (dataModel.getNama_status()) {
				case "Perbaikan Selesai":
					txt_status.setText("Sudah ditangani");
					txt_status.setBackgroundResource(R.color.green);
					txt_status.setTextColor(Color.parseColor("#FFFFFF"));
					break;
				case "Proses Perbaikan":
					txt_status.setText("Proses ditangani");
					txt_status.setBackgroundResource(R.color.yellow);
					txt_status.setTextColor(Color.parseColor("#FFFFFF"));
					break;
				default:
					txt_status.setText("Belum ditangani");
					txt_status.setBackgroundResource(R.color.red);
					txt_status.setTextColor(Color.parseColor("#FFFFFF"));
					break;
			}
		}

		@Override
		public void onClick(View view) {
			Intent i = new Intent(context, DetailListDataActivity.class);
			i.putExtra("id_kerusakan", txt_id_kerusakan.getText().toString());
			context.startActivity(i);
		}
	}
}
