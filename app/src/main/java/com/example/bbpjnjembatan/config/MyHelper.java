package com.example.bbpjnjembatan.config;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyHelper {
    public static void showDateDialogMax(Context mContext, EditText view, String format) {
        long today = Calendar.getInstance().getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl = view.getText().toString().split("-");
            if (tgl[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.getDatePicker().setMaxDate(today);
        datePickerDialog.show();
    }

    // overloading
    public static void showDateDialogMax(Context mContext, EditText view) {
        long today = Calendar.getInstance().getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl = view.getText().toString().split("-");
            if (tgl[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.getDatePicker().setMaxDate(today);
        datePickerDialog.show();
    }

    public static void showDateDialogMin(Context mContext, EditText view) {
        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl = view.getText().toString().split("-");
            if (tgl[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showDateDialog(Context mContext, EditText view) {
        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl = view.getText().toString().split("-");
            if (tgl[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.show();
    }

    public static void showDateDialogMinReset(Context mContext, EditText view, EditText et_akhir) {
        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl = view.getText().toString().split("-");
            if (tgl[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);
            et_akhir.setText("");

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showDateDialogWithCustomMinDay(Context mContext, EditText view, String date) {
        // 18-10-2001
        String[] tgl = date.split("-");
        Calendar min_date = Calendar.getInstance();
        min_date.set(Integer.parseInt(tgl[2]), Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[0]));
        Date d = min_date.getTime();
        long dateLong = d.getTime();

        Calendar newCalendar = Calendar.getInstance();

        if (view.getText().toString().length() > 0) {
            String[] tgl_lama = view.getText().toString().split("-");
            if (tgl_lama[2].length() == 4)
                newCalendar.set(Integer.parseInt(tgl_lama[2]), Integer.parseInt(tgl_lama[1]) - 1, Integer.parseInt(tgl_lama[0]));
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (datePicker, selectedYear, selectedMonth, selectedDate) -> {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDate);
            view.setText(dateFormatter.format(c.getTime()));
            view.setError(null);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datePickerDialog.getDatePicker().setMinDate(dateLong);
        datePickerDialog.show();
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    public static String getStringImage(Bitmap bmp, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String convertDateIdNum(String tanggalLama) {
        SimpleDateFormat _date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // maximal length 9 (yyyy-MM-dd)
        String actual = tanggalLama;
        String output = "-";

        Date date1 = null;
        try {
            date1 = sdf.parse(actual);

            output = _date.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) {
        try {
            if (date.equals(""))
                return "";
        } catch (NullPointerException e) {
            return "";
        }

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat(initDateFormat).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = null;
        if (initDate != null) {
            parsedDate = formatter.format(initDate);
        }
        return parsedDate;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static boolean isEmailValid(String email) {
        Log.e("EMAILVALIDASI", email);
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String convertIdDate(String tgl) {
        String[] month = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };

        // pecah tanggal
        try {
            if (tgl != null) {
                String[] result = tgl.split(" ");
                for (int i = 0; i < month.length; i++) {
                    if (result[1].equals(month[i])) {
                        DecimalFormat df = new DecimalFormat("00");
                        result[1] = df.format(i + 1);
                    }
                }
                return result[0] + "-" + result[1] + "-" + result[2];
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return "";
        }
        return "";
    }
}
