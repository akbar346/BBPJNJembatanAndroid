package com.example.bbpjnjembatan;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sdsmdg.tastytoast.TastyToast;

class DialogPreview {
    static void dialogPreview(Context mContext, String url) {
        Dialog showPreview = new Dialog(mContext);
        showPreview.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showPreview.setContentView(R.layout.dialog_preview);
        ImageView imageViewBack = showPreview.findViewById(R.id.imv_preview);
        Button btn_kembali = showPreview.findViewById(R.id.btn_back);

        if (url != null && !url.trim().isEmpty()) {
            Glide.with(mContext)
                    .load(url)
                    .into(imageViewBack);
        } else {
            TastyToast.makeText(mContext, "Data Kosong!", TastyToast.LENGTH_SHORT, TastyToast.INFO);
            imageViewBack.setImageDrawable(mContext.getResources().getDrawable(R.drawable.defaultimg));
        }

        btn_kembali.setOnClickListener(view -> showPreview.dismiss());
        showPreview.setCancelable(false);
        showPreview.show();
    }
}
