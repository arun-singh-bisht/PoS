package com.posfone.promote.posfone.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public  interface ImageDownloadListener
    {
        public void imageDownloaded(String imageName);
    }

    public static void saveImage(Context context,String imageUrl,ImageDownloadListener imageDownloadListener)
    {
        String fileName = imageUrl.substring( imageUrl.lastIndexOf('/')+1, imageUrl.length() );
        Picasso.with(context).load(imageUrl).into(picassoImageTarget(context, "PoSfoneImageDir", fileName,imageDownloadListener));
    }

    public static void loadImageInImageView(Context context, String imageUrl, ImageView imageView)
    {
        String fileName = imageUrl.substring( imageUrl.lastIndexOf('/')+1, imageUrl.length() );

        File directory =  new File(Environment.getExternalStorageDirectory(),"PoSfoneImageDir");
        File myImageFile = new File(directory, fileName);
        Picasso.with(context).load(myImageFile).into(imageView);
    }


    private static Target picassoImageTarget(Context context, final String imageDir, final String imageName,final ImageDownloadListener imageDownloadListener) {

        Log.d("ImageUtil", " picassoImageTarget");


        ContextWrapper cw = new ContextWrapper(context);
        final File directory = new File(Environment.getExternalStorageDirectory(),imageDir); // path to /data/data/yourapp/app_imageDir

        if(!directory.exists()) {
            boolean b =  directory.mkdir();
            Log.d("ImageUtil", " directory.mkdir(): "+b);
        }

        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            if(imageDownloadListener!=null)
                            imageDownloadListener.imageDownloaded(imageName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("ImageUtil", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.i("ImageUtil", "onBitmapFailed");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    public static int getImageRotation(Context context, Uri imageUri) {
        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            if (rotation == ExifInterface.ORIENTATION_UNDEFINED)
                return getRotationFromMediaStore(context, imageUri);
            else return exifToDegrees(rotation);
        } catch (IOException e) {
            return 0;
        }
    }
    public static int getRotationFromMediaStore(Context context, Uri imageUri) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
        if (cursor == null) return 0;

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
        return cursor.getInt(orientationColumnIndex);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }

}
