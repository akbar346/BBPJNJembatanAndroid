package com.example.bbpjnjembatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bbpjnjembatan.adapter.ListDataAdapter;
import com.example.bbpjnjembatan.config.BaseApiService;
import com.example.bbpjnjembatan.config.UtilsApi;
import com.example.bbpjnjembatan.roomDB.db.AppDatabaseRoom;
import com.example.bbpjnjembatan.roomDB.entity.item.DataJalanItems;
import com.example.bbpjnjembatan.roomDB.entity.rest.RestDataJalan;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListDataActivity extends AppCompatActivity {
	@BindView(R.id.btn_pdf)
	Button btn_pdf;
	@BindView(R.id.recycler)
	RecyclerView recyclerView;
	private Context mContext;
	private AppDatabaseRoom db;
	private BaseApiService mApiService;
	private Integer limit = 3, page = 1;
	private RestDataJalan jalan;
	private List<DataJalanItems> hasilItems = new ArrayList<>();
	private ListDataAdapter listDataAdapter;
	private String nama, jabatan, id_user, id_ppk, id_satker, tgl_export, tgl_ttd, satker, ppk;
	private String[] permissions = new String[]{
			android.Manifest.permission.INTERNET,
			android.Manifest.permission.READ_PHONE_STATE,
			android.Manifest.permission.READ_EXTERNAL_STORAGE,
			android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
			android.Manifest.permission.VIBRATE,
			Manifest.permission.RECORD_AUDIO,
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_data);
		ButterKnife.bind(this);

		Toolbar mToolbar = findViewById(R.id.main_toolbar);
		setSupportActionBar(mToolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle("List Data");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(view -> finish());

		mContext = this;
		mApiService = UtilsApi.getAPIService();
		db = Room.databaseBuilder(getApplicationContext(), AppDatabaseRoom.class, "db_bbpjn").allowMainThreadQueries().fallbackToDestructiveMigration().build();
		checkPermissions();

		jabatan = db.userDao().selectData().getNama_jabatan();
		id_user = db.userDao().selectData().getId_user();
		id_ppk = db.userDao().selectData().getId_ppk();
		id_satker = db.userDao().selectData().getId_satker();
		satker = db.userDao().selectData().getNama_satker();
		ppk = db.userDao().selectData().getNama_ppk();
		nama = db.userDao().selectData().getNama_lengkap();

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

		Calendar calendar = Calendar.getInstance();
		@SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		@SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("E, dd MM yyyy");
		tgl_export = simpleDateFormat.format(calendar.getTime());
		tgl_ttd = simpleDateFormat2.format(calendar.getTime());
		btn_pdf.setOnClickListener(v -> {
			List<DataJalanItems> datalist = listDataAdapter.getDatalist();
			DataJalanItems dataSet = null;
			int i, no = 1;
			for (i = 0; i < datalist.size(); i++) {
				dataSet = datalist.get(i);
				if (dataSet.isSelected()) {
					id_user += dataSet.getId_input();
				}
			}

			try {
				if (id_user == null) {
					TastyToast.makeText(mContext, "Data Belum Terpilih", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
				} else {
					Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
					Rectangle rect = new Rectangle(PageSize.A4.rotate());
					rect.setBorderColor(BaseColor.BLUE);
					rect.setBorderWidth(6);
					rect.setBorderWidthLeft(6);
					rect.setBorderWidthRight(6);
					rect.setBorder(Rectangle.BOX);
					Document document = new Document(rect);
					String path = Environment.getExternalStorageDirectory() + "/" + "BBPJN/";
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					try {
						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + tgl_export + ".pdf"));
						MyFooter event = new MyFooter();
						writer.setPageEvent(event);
					} catch (DocumentException | FileNotFoundException e1) {
						e1.printStackTrace();
					}

					document.open();
					Font titleFont = new Font(Font.FontFamily.UNDEFINED, 14, Font.BOLD);
					Font foot12 = new Font(Font.FontFamily.UNDEFINED, 12, Font.BOLD);
					Font foot = new Font(Font.FontFamily.UNDEFINED, 12, Font.NORMAL);
					Paragraph prHead = new Paragraph();
					prHead.setFont(titleFont);
					prHead.setAlignment(Element.ALIGN_CENTER);
					prHead.add("Formulir Penilikan Jalan\nDirektorat Jenderal Bina Marga \n \n");

					Paragraph prFoot = new Paragraph();
					prFoot.setFont(foot);
					prFoot.add(tgl_ttd + "\n\n\n\n\n");
					prFoot.setAlignment(Element.ALIGN_RIGHT);

					Paragraph prFoot1 = new Paragraph();
					prFoot1.setFont(foot12);
					prFoot1.add(" " + nama);
					prFoot1.setAlignment(Element.ALIGN_RIGHT);

					PdfPTable table1 = new PdfPTable(6);
					table1.setWidthPercentage(105);
					try {
						table1.setWidths(new int[]{2, 1, 3, 2, 1, 2});
					} catch (DocumentException e1) {
						e1.printStackTrace();
					}
					table1.addCell(createCell("S a t k e r", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell(":", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell(satker, 1, 1, Element.ALIGN_CENTER, Element.ALIGN_JUSTIFIED, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_JUSTIFIED, Rectangle.NO_BORDER));
					table1.addCell(createCell("P P K", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell(":", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell(ppk, 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_JUSTIFIED, Rectangle.NO_BORDER));
					table1.addCell(createCell("T a n g g a l  E x p o r t", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell(":", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell(tgl_export, 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Rectangle.NO_BORDER));
					table1.addCell(createCell("", 1, 1, Element.ALIGN_CENTER, Element.ALIGN_JUSTIFIED, Rectangle.NO_BORDER));

					table1.setSpacingAfter(15f);
					PdfPTable table = new PdfPTable(18);
					table.setWidthPercentage(105);
					try {
						table.setWidths(new int[]{2, 3, 3, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3});
					} catch (DocumentException e1) {
						e1.printStackTrace();
					}
					PdfPCell cell;
					cell = new PdfPCell(new Phrase("No", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(6);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Nama Penilik", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(6);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Kategori Kerusakan", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(5);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Jenis Kerusakan", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(5);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Status Jalan", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(6);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Tanggal Pelaporan", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(6);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase("Keterangan", boldFont));
					cell.setBorderWidth(1);
					cell.setRowspan(6);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					Image image = null;
					byte[] byteArray = stream.toByteArray();
					try {
						image = Image.getInstance(byteArray);
					} catch (BadElementException | IOException e) {
						e.printStackTrace();
					}

					Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.closed);
					ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
					bm2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
					Image image2 = null;
					byte[] byteArray2 = stream2.toByteArray();
					try {
						image2 = Image.getInstance(byteArray2);
					} catch (BadElementException | IOException e) {
						e.printStackTrace();
					}

					for (i = 0; i < datalist.size(); i++) {
						dataSet = datalist.get(i);
						if (dataSet.isSelected()) {
							if (dataSet.getId_kerusakan() != null) {
								table.addCell(createCell2(String.valueOf(no++), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getNama_input(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getNama_kategori(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getNama_kerusakan(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getNama_tingkat(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getTgl_pengecekan(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
								table.addCell(createCell2(dataSet.getDetail_kerusakan(), 1, 2, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER));
							} else {
								Log.i("Tag: ", "Data kosong");
							}
						}
					}

					table.setSpacingAfter(15f);

					try {
						document.add(prHead);
						document.add(table1);
						document.add(table);
						document.add(prFoot);
						document.add(prFoot1);
					} catch (DocumentException e1) {
						e1.printStackTrace();
					}

					document.close();
					TastyToast.makeText(mContext, "File Berhasil Di Unduh\nLokasi : /storage/BBPJN/", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void sendPost() {
		switch (jabatan) {
			case "Penilik":
				loadDataPenilik(id_user, limit, (page - 1) * limit);
				break;
			case "PPK":
				loadDataPpk(id_ppk, limit, (page - 1) * limit);
				break;
			case "Satker":
				loadDataSatker(id_satker, limit, (page - 1) * limit);
				break;
			default:
				Log.e("Error : ", "Load gagal");
				break;
		}
	}

	private void loadDataSatker(String id_satker, int limit, int offset) {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanSatkerPagingRequest(id_satker, limit, offset)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(response.body().getHasil());

								listDataAdapter = new ListDataAdapter(mContext, hasilItems);
								listDataAdapter.setLoadMoreListener(() -> recyclerView.post(() -> {
									page++;
									loadmoreDataSatker(id_satker, limit, page);
								}));

								recyclerView.setHasFixedSize(true);
								recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
								recyclerView.addItemDecoration(new VerticalLineDecorator(2));
								recyclerView.setAdapter(listDataAdapter);

								for (int i = 0; i < hasilItems.size(); i++) {
									hasilItems.get(i).setType("datas");
								}
								jalan = response.body();
								listDataAdapter.notifyDataChanged();
							} else {
//                                hasilItems.remove(hasilItems.size() - 1);
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

	private void loadmoreDataSatker(String id_satker, int limit, int index) {
		hasilItems.add(new DataJalanItems("load"));
		listDataAdapter.notifyItemInserted(hasilItems.size() - 1);

		mApiService.dataJalanSatkerPagingRequest(id_satker, limit, (index - 1) * limit)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						if (response.isSuccessful()) {
							hasilItems.remove(hasilItems.size() - 1);
							List<DataJalanItems> result = response.body().getHasil();

							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(result);
							} else {
								listDataAdapter.setMoreDataAvailable(false);
							}
							for (int i = 0; i < hasilItems.size(); i++) {
								hasilItems.get(i).setType("datas");
							}
							jalan = response.body();
							listDataAdapter.notifyDataChanged();
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void loadDataPpk(String id_ppk, int limit, int offset) {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanPpkPagingRequest(id_ppk, limit, offset)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(response.body().getHasil());

								listDataAdapter = new ListDataAdapter(mContext, hasilItems);
								listDataAdapter.setLoadMoreListener(() -> recyclerView.post(() -> {
									page++;
									loadmoreDataPpk(id_ppk, limit, page);
								}));

								recyclerView.setHasFixedSize(true);
								recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
								recyclerView.addItemDecoration(new VerticalLineDecorator(2));
								recyclerView.setAdapter(listDataAdapter);

								for (int i = 0; i < hasilItems.size(); i++) {
									hasilItems.get(i).setType("datas");
								}
								jalan = response.body();
								listDataAdapter.notifyDataChanged();
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

	private void loadmoreDataPpk(String id_ppk, int limit, int index) {
		hasilItems.add(new DataJalanItems("load"));
		listDataAdapter.notifyItemInserted(hasilItems.size() - 1);

		mApiService.dataJalanPpkPagingRequest(id_ppk, limit, (index - 1) * limit)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						if (response.isSuccessful()) {
							hasilItems.remove(hasilItems.size() - 1);
							List<DataJalanItems> result = response.body().getHasil();

							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(result);
							} else {
								listDataAdapter.setMoreDataAvailable(false);
							}
							for (int i = 0; i < hasilItems.size(); i++) {
								hasilItems.get(i).setType("datas");
							}
							jalan = response.body();
							listDataAdapter.notifyDataChanged();
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	private void loadDataPenilik(String id_pegawai, int limit, int offset) {
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Mohon tunggu...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		mApiService.dataJalanPegawaiPagingRequest(id_pegawai, limit, offset)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						progressDialog.dismiss();
						if (response.isSuccessful()) {
							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(response.body().getHasil());

								listDataAdapter = new ListDataAdapter(mContext, hasilItems);
								listDataAdapter.setLoadMoreListener(() -> recyclerView.post(() -> {
									page++;
									loadmoreDataPenilik(id_pegawai, limit, page);
								}));
								recyclerView.setHasFixedSize(true);
								recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
								recyclerView.addItemDecoration(new VerticalLineDecorator(2));
								recyclerView.setAdapter(listDataAdapter);

								for (int i = 0; i < hasilItems.size(); i++) {
									hasilItems.get(i).setType("datas");
								}
								jalan = response.body();
								listDataAdapter.notifyDataChanged();
							} else {
//                                hasilItems.remove(hasilItems.size() - 1);
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

	private void loadmoreDataPenilik(String id_pegawai, int limit, int index) {
		hasilItems.add(new DataJalanItems("load"));
		listDataAdapter.notifyItemInserted(hasilItems.size() - 1);

		mApiService.dataJalanPegawaiPagingRequest(id_pegawai, limit, (index - 1) * limit)
				.enqueue(new Callback<RestDataJalan>() {
					@Override
					public void onResponse(@NonNull Call<RestDataJalan> call, @NonNull Response<RestDataJalan> response) {
						if (response.isSuccessful()) {
							hasilItems.remove(hasilItems.size() - 1);
							List<DataJalanItems> result = response.body().getHasil();

							if (response.body().getSuccess().equals("true")) {
								hasilItems.addAll(result);
							} else {
								listDataAdapter.setMoreDataAvailable(false);
							}
							for (int i = 0; i < hasilItems.size(); i++) {
								hasilItems.get(i).setType("datas");
							}
							jalan = response.body();
							listDataAdapter.notifyDataChanged();
						} else {
							TastyToast.makeText(mContext, "Web Service tidak merespon", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
						}
					}

					@Override
					public void onFailure(@NonNull Call<RestDataJalan> call, @NonNull Throwable t) {
						Log.e("debug", "onFailure: ERROR > " + t.toString());
					}
				});
	}

	public PdfPCell createCell(String content, float borderWidth, int colspan, int alignment, int alignmenthori, int border) {
		FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
		PdfPCell cell = new PdfPCell(new Phrase(content));
		cell.setBorderWidth(borderWidth);
		cell.setBorder(border);
		cell.setColspan(colspan);
		cell.setVerticalAlignment(alignment);
		cell.setHorizontalAlignment(alignmenthori);
		return cell;
	}

	public PdfPCell createCell2(String content, float borderWidth, int rowsapn, int colspan, int alignment, int alignmenthori) {
		FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
		PdfPCell cell = new PdfPCell(new Phrase(content));
		cell.setBorderWidth(borderWidth);
		cell.setRowspan(rowsapn);
		cell.setColspan(colspan);
		cell.setVerticalAlignment(alignment);
		cell.setHorizontalAlignment(alignmenthori);
		return cell;
	}

	private void checkPermissions() {
		int result;
		List<String> listPermissionsNeeded = new ArrayList<>();
		for (String p : permissions) {
			result = ContextCompat.checkSelfPermission(mContext, p);
			if (result != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(p);
			}
		}
		if (!listPermissionsNeeded.isEmpty()) {
			ActivityCompat.requestPermissions((Activity) mContext, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
		}
	}

	class MyFooter extends PdfPageEventHelper {
		Font ffont = new Font(Font.getFamily(String.valueOf(R.font.montserrat)), 5, Font.ITALIC);

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			int a = writer.getPageNumber();
			Phrase footer = null;
			for (int i = 0; i < a; ) {
				i++;
				footer = new Phrase(nama + "/" + tgl_export + "/" + i, ffont);
			}
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}
}
