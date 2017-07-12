package com.repositoryworks.datarepository.utils.fileUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.repositoryworks.datarepository.MainActivity;
import com.repositoryworks.datarepository.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ajay3 on 7/7/2017.
 */

public class FileUtilities {

    /**
     * Get file byte array from its file path
     * @param imgPath
     * @return returns the file byte array
     * @throws IOException
     */
    @Nullable
    public static byte[] getFileBytes(String imgPath) throws IOException {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = BitmapFactory.decodeFile(imgPath,options);
        if(bmp != null){
            bmp = Bitmap.createScaledBitmap(bmp, (int) (MainActivity.sDisplayMetrics.widthPixels/2.7),
                    (int) (MainActivity.sDisplayMetrics.heightPixels/5.7),true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }else{
            return null;
        }
    }

    /**
     * Get image bitmap
     * @param image
     * @return return the bitmap
     */
    public static Bitmap getImageBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    /**
     * Get file path from content uri
     * @param context
     * @param contentUri
     * @return returns the file location
     */
    public static String getFilePath(Context context, Uri contentUri){

        String result = "";

        if(contentUri.getPath().contains(context.getString(R.string.storage))){
            result = contentUri.getPath().split("/file")[1];

        }else{
            String[] projection = { MediaStore.Images.Media.DATA };
            String where = MediaStore.Images.Media._ID + " = ?";
            String id = "";

            if(contentUri.getEncodedPath().contains("%3A")){
                id = contentUri.getEncodedPath().replaceFirst("%3A",":");
                id = id.substring(id.indexOf(':')+1);
            }else{
                id = contentUri.getPath();
                id = id.substring(id.lastIndexOf('/')+1);
            }

            String[] whereArgs = {id};

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, where, whereArgs, null);

            if(cursor!= null && cursor.moveToFirst()){
                result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                cursor.close();
            }else {
                result = context.getString(R.string.no_result);
            }
        }

        return result;
    }
}