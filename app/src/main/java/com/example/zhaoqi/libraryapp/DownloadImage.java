package com.example.zhaoqi.libraryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.zhaoqi.libraryapp.R;

import java.io.InputStream;

/**
 * Created by Zhaoqi on 5/14/18.
 */

class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;

    public DownloadImage(ImageView imageView){
        this.imageView = imageView;
    }


    protected Bitmap doInBackground(String... urls){
        String url = urls[0];
        Bitmap map = null;
        try{
            InputStream in = new java.net.URL(url).openStream();
            map = BitmapFactory.decodeStream(in);

        }catch(Exception e){
            return null;
        }

        return map;
    }

    protected void onPostExecute(Bitmap result){
        if(result == null){
            imageView.setImageResource(R.drawable.puppy);
        }else {
            imageView.setImageBitmap(result);
        }
    }
}
