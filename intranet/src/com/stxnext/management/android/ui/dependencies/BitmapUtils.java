
package com.stxnext.management.android.ui.dependencies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.stxnext.management.android.web.HttpClientProvider;

import ch.boye.httpclientandroidlib.client.HttpClient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BitmapUtils {

    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }


    public static Drawable scaleDrawable(Resources res, Drawable original, float percentage) {
        Bitmap bitmap = ((BitmapDrawable)original).getBitmap();

        int newWidth = (int)(bitmap.getWidth() * percentage);
        int newHeight = (int)(bitmap.getHeight() * percentage);

        Drawable result = new BitmapDrawable(res, Bitmap.createScaledBitmap(bitmap, newWidth,
                newHeight, true));

        return result;
    }

    public static Bitmap scaleBitmap(int maxWidth, int maxHeight, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = 1;

        if (height > width) {
            if (height > maxHeight) {
                scale = (float)maxHeight / (float)height;
            }
        } else {
            if (width > maxWidth) {
                scale = (float)maxWidth / (float)width;
            }
        }

        width = (int)(width * scale);
        height = (int)(height * scale);

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    

    public static synchronized Bitmap decodeSampledBitmapFromFile(String path) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.outMimeType = "image/jpeg";
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inTempStorage = new byte[256];

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.outMimeType = "image/jpeg";
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inTempStorage = new byte[1024 * 16];

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getBitmapFromResource(int resourceId, Resources res, Integer sampleSize) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize == null ? 1 : sampleSize;
        options.outMimeType = "image/jpeg";
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inTempStorage = new byte[256];

        return BitmapFactory.decodeResource(res, resourceId, options);
    }

    public static synchronized Bitmap getBitmapFromResourceSynchronized(int resourceId,
            Resources res, Integer sampleSize) {
        return getBitmapFromResource(resourceId, res, sampleSize);
    }

    public static Bitmap decodeSampledBitmapFromResource(InputStream is, int reqWidth, int reqHeight)
            throws IOException {

        byte[] data = IOUtils.toByteArray(is);
        return decodeSampledBitmapFromResource(data, reqWidth, reqHeight);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (format == null) {
            format = Bitmap.CompressFormat.JPEG;
        }

        bitmap.compress(format, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private static synchronized File getTempDirectory(Context context) {
        File tempDir = new File(context.getFilesDir(), "temps/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return tempDir;
    }

    public static synchronized void saveTempBitmap(Context context, Bitmap bitmap, String filename) {
        File file = new File(getTempDirectory(context), filename);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            Log.e(BitmapUtils.class.getName(),"",e);
        } catch (IOException e) {
            Log.e(BitmapUtils.class.getName(),"",e);
        }
    }

    public static synchronized Bitmap getTempBitmap(Context context, String filename) {
        File path = new File(getTempDirectory(context), filename);
        return decodeSampledBitmapFromFile(path.getAbsolutePath());
    }

    public static synchronized void cleanTempDir(Context context) {
        File tempDir = getTempDirectory(context);
        String[] children = tempDir.list();
        for (int i = 0; i < children.length; i++) {
            new File(tempDir, children[i]).delete();
        }
        tempDir.delete();
    }

}
