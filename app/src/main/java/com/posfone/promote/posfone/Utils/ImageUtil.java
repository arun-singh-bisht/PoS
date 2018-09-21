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

    @TargetApi(Build.VERSION_CODES.N)
    public static Bitmap rotateImageIfNeeded(Context context, Uri imageUri)
    {
        int rotate = 0;
        try {
            String photoPAth=getRealPathFromUri(imageUri,context);

           InputStream stream= context.getContentResolver().openInputStream(imageUri);
            File imageFile = new File(imageUri.getPath());
            ExifInterface exif = new ExifInterface(photoPAth);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("rotateImageIfNeeded","Exif orientation:"+orientation);

            Bitmap rotattedBitmap= BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            stream.close();
            return Bitmap.createBitmap(rotattedBitmap, 0, 0, rotattedBitmap.getWidth(), rotattedBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri(Uri uri,Context context){
        String filePath="";
        String wholeid= DocumentsContract.getDocumentId(uri);
        String id=wholeid.split(":")[1];
        String [] coloumn={MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver() .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, coloumn, sel, new String[]{ id }, null);
        int columnIndex = cursor.getColumnIndex(coloumn[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        return orientation;
    }


}
