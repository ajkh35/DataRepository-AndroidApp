package com.repositoryworks.datarepository.utils.fileUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ajay3 on 7/8/2017.
 */

public class NetworkFileUtilities {

    /**
     * Create bitmap from network url path
     * @param url
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFromUrl(String url) throws IOException {
        URL web_url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) web_url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    /**
     * Get byte array output stream
     * @param url
     * @return returns byte array after getting from network
     */
    public static byte[] getByteArrayFromUrl(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int current = 0;
            while ((current = bis.read()) != -1) {
                stream.write((byte) current);
            }

            return stream.toByteArray();
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }
}