package com.posfone.promote.posfone.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.posfone.promote.posfone.R;


public class PickerDialog extends Dialog implements View.OnClickListener {
    Context context;
    ImagePickerListener imagePickerListener;

    public void setImagePickerListener(ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }

    public PickerDialog(@NonNull Context context) {
        super(context);
        this.context=context;
        init();
    }

    private void init() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.picker_dialog_layout, null);
        setContentView(layout);

        getWindow().setBackgroundDrawableResource(R.color.white);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.windowAnimations = R.style.Error_dialog_style;
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;

        setTitle(null);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnCancelListener(null);
        LinearLayout gallery=(LinearLayout)findViewById(R.id.gallery);
        LinearLayout camera=(LinearLayout)findViewById(R.id.camera);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera:
                dismiss();
                if (imagePickerListener!=null){
                    imagePickerListener.onCameraSelect();
                }
                break;
            case R.id.gallery:
                dismiss();
                if (imagePickerListener!=null){
                    imagePickerListener.onGallerySelect();
                }
                break;
        }
    }

    public interface ImagePickerListener{
        void onGallerySelect();
        void onCameraSelect();
    }
}
