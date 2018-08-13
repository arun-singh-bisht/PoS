package com.posfone.promote.posfone.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicasoImageUtil {

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

        Log.d("PicasoImageUtil", " picassoImageTarget");


        ContextWrapper cw = new ContextWrapper(context);
        final File directory = new File(Environment.getExternalStorageDirectory(),imageDir); // path to /data/data/yourapp/app_imageDir

        if(!directory.exists()) {
            boolean b =  directory.mkdir();
            Log.d("PicasoImageUtil", " directory.mkdir(): "+b);
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
                        Log.i("PicasoImageUtil", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.i("PicasoImageUtil", "onBitmapFailed");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }
}
